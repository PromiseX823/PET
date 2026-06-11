
package com.example.app.service;

import com.example.app.dto.request.AdoptionRequest;
import com.example.app.dto.response.AdoptionResponse;
import com.example.app.dto.response.PageResponse;
import com.example.app.entity.Adoption;
import com.example.app.entity.Pet;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.AdoptionRepository;
import com.example.app.repository.PetRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final DistributedLockService distributedLockService;

    @Transactional(readOnly = true)
    public PageResponse<AdoptionResponse> getAdoptions(String status, Long userId, Long petId, 
                                                       int page, int pageSize) {
        Sort sort = Sort.by("applyTime").descending();
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        
        Page<Adoption> adoptionPage = adoptionRepository.findByFilters(status, userId, petId, pageable);
        return PageResponse.from(adoptionPage.map(this::toAdoptionResponse));
    }

    @Transactional(readOnly = true)
    public AdoptionResponse getAdoption(Long adoptionId) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new BusinessException("领养申请不存在"));
        return toAdoptionResponse(adoption);
    }

    @Transactional
    public AdoptionResponse createAdoption(AdoptionRequest request) {
        String lockKey = "adoption:pet:" + request.getPetId() + ":user:" + request.getUserId();
        
        String lockValue = distributedLockService.tryLockWithRetry(lockKey, 30, 3, 100);
        if (lockValue == null) {
            log.warn("获取分布式锁失败，可能存在并发提交: petId={}, userId={}", request.getPetId(), request.getUserId());
            throw new BusinessException("请求过于频繁，请稍后重试");
        }
        
        try {
            Pet pet = petRepository.findById(request.getPetId())
                    .orElseThrow(() -> new BusinessException("宠物不存在"));

            if (!"待领养".equals(pet.getStatus()) && !"available".equalsIgnoreCase(pet.getStatus())) {
                throw new BusinessException("该宠物暂不可领养");
            }

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            if (adoptionRepository.existsActiveAdoption(request.getPetId(), request.getUserId())) {
                throw new BusinessException("您已经申请过该宠物");
            }

            Adoption adoption = Adoption.builder()
                    .petId(request.getPetId())
                    .applicantId(request.getUserId())
                    .status("pending")
                    .applicantNote(request.getReason() != null ? request.getReason() : "")
                    .build();

            adoption = adoptionRepository.save(adoption);

            notificationService.createNotification(
                    1L,
                    "领养申请",
                    "用户 " + user.getUsername() + " 申请领养宠物 " + pet.getName(),
                    "adoption_request",
                    adoption.getId()
            );

            return toAdoptionResponse(adoption);
        } finally {
            distributedLockService.releaseLock(lockKey, lockValue);
        }
    }

    @Transactional
    public void approveAdoption(Long adoptionId, Long reviewerId, String reviewComment) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new BusinessException("领养申请不存在"));

        if (!"pending".equals(adoption.getStatus()) && !"待审核".equals(adoption.getStatus())) {
            throw new BusinessException("该申请已被处理");
        }

        Pet pet = petRepository.findById(adoption.getPetId())
                .orElseThrow(() -> new BusinessException("宠物不存在"));

        adoption.setStatus("approved");
        adoption.setReviewTime(LocalDateTime.now());
        adoption.setAdminNote(reviewComment);
        adoptionRepository.save(adoption);

        pet.setStatus("adopted");
        petRepository.save(pet);

        List<Adoption> otherAdoptions = adoptionRepository.findOtherPendingAdoptions(adoption.getPetId(), adoptionId);
        for (Adoption other : otherAdoptions) {
            other.setStatus("rejected");
            other.setReviewTime(LocalDateTime.now());
            other.setAdminNote("该宠物已被其他用户领养");
            adoptionRepository.save(other);

            notificationService.createNotification(
                    other.getApplicantId(),
                    "领养申请被拒绝",
                    "您申请领养的宠物 " + pet.getName() + " 已被其他用户领养",
                    "adoption_rejected",
                    other.getId()
            );
        }

        User user = userRepository.findById(adoption.getApplicantId()).orElse(null);
        if (user != null) {
            notificationService.createNotification(
                    user.getId(),
                    "领养申请已批准",
                    "您申请领养的宠物 " + pet.getName() + " 已被批准",
                    "adoption_approved",
                    adoption.getId()
            );
        }
    }

    @Transactional
    public void rejectAdoption(Long adoptionId, Long reviewerId, String reviewComment) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new BusinessException("领养申请不存在"));

        if (!"pending".equals(adoption.getStatus()) && !"待审核".equals(adoption.getStatus())) {
            throw new BusinessException("该申请已被处理");
        }

        adoption.setStatus("rejected");
        adoption.setReviewTime(LocalDateTime.now());
        adoption.setAdminNote(reviewComment);
        adoptionRepository.save(adoption);

        Pet pet = petRepository.findById(adoption.getPetId()).orElse(null);
        User user = userRepository.findById(adoption.getApplicantId()).orElse(null);

        if (user != null && pet != null) {
            notificationService.createNotification(
                    user.getId(),
                    "领养申请未被批准",
                    "您申请领养的宠物 " + pet.getName() + " 未被批准",
                    "adoption_rejected",
                    adoption.getId()
            );
        }
    }

    @Transactional
    public void cancelAdoption(Long adoptionId) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new BusinessException("领养申请不存在"));

        if (!"pending".equals(adoption.getStatus()) && !"待审核".equals(adoption.getStatus())) {
            throw new BusinessException("只能取消待处理的申请");
        }

        adoption.setStatus("cancelled");
        adoption.setReviewTime(LocalDateTime.now());
        adoptionRepository.save(adoption);
    }

    @Transactional
    public void completeAdoption(Long adoptionId) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new BusinessException("领养申请不存在"));

        if (!"approved".equals(adoption.getStatus())) {
            throw new BusinessException("只能完成已批准的申请");
        }

        adoption.setStatus("completed");
        adoption.setReviewTime(LocalDateTime.now());
        adoptionRepository.save(adoption);

        User user = userRepository.findById(adoption.getApplicantId()).orElse(null);
        Pet pet = petRepository.findById(adoption.getPetId()).orElse(null);

        if (user != null && pet != null) {
            notificationService.createNotification(
                    user.getId(),
                    "领养完成",
                    "恭喜！您已成功领养宠物 " + pet.getName(),
                    "adoption_completed",
                    adoption.getId()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<AdoptionResponse> getPetAdoptions(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));

        return adoptionRepository.findByPetId(petId).stream()
                .map(this::toAdoptionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdoptionResponse> getUserAdoptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        return adoptionRepository.findByApplicantId(userId).stream()
                .map(this::toAdoptionResponse)
                .toList();
    }

    private AdoptionResponse toAdoptionResponse(Adoption adoption) {
        Pet pet = petRepository.findById(adoption.getPetId()).orElse(null);
        return AdoptionResponse.builder()
                .id(adoption.getId())
                .petId(adoption.getPetId())
                .applicantId(adoption.getApplicantId())
                .status(adoption.getStatus())
                .applyTime(adoption.getApplyTime())
                .reviewTime(adoption.getReviewTime())
                .applicantNote(adoption.getApplicantNote())
                .adminNote(adoption.getAdminNote())
                .pet(pet != null ? PetService.toPetSimpleResponse(pet) : null)
                .user(userService.getUserSimple(adoption.getApplicantId()))
                .build();
    }
}
