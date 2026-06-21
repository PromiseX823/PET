
package com.example.app.service;

import com.example.app.dto.request.PetCreateRequest;
import com.example.app.dto.response.PageResponse;
import com.example.app.dto.response.PetResponse;
import com.example.app.dto.response.PetSimpleResponse;
import com.example.app.entity.Pet;
import com.example.app.entity.Photo;
import com.example.app.exception.BusinessException;
import com.example.app.repository.PetRepository;
import com.example.app.repository.PhotoRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PhotoRepository photoRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public PageResponse<PetResponse> getPets(String type, String status, String breed, 
                                              String gender, String location,
                                              int page, int pageSize, String sortBy, String sortOrder) {
        String entitySortBy = convertToCamelCase(sortBy);
        Sort sort = sortOrder.equalsIgnoreCase("desc") 
                ? Sort.by(entitySortBy).descending() 
                : Sort.by(entitySortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        
        Specification<Pet> spec = buildSpecification(type, status, breed, gender, location);
        Page<Pet> petPage = petRepository.findAll(spec, pageable);
        return PageResponse.from(petPage.map(this::toPetResponse));
    }
    
    private String convertToCamelCase(String input) {
        if (input == null || !input.contains("_")) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : input.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                sb.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private Specification<Pet> buildSpecification(String type, String status, String breed, 
                                                  String gender, String location) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(type)) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(breed)) {
                predicates.add(criteriaBuilder.like(root.get("breed"), "%" + breed + "%"));
            }
            if (StringUtils.hasText(gender)) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }
            if (StringUtils.hasText(location)) {
                predicates.add(criteriaBuilder.like(root.get("location"), "%" + location + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));
        return toPetResponse(pet);
    }

    @Transactional
    public PetResponse createPet(PetCreateRequest request) {
        if (request.getPhotos() == null || request.getPhotos().isEmpty()) {
            throw new BusinessException("宠物必须至少有一张照片");
        }
        
        boolean hasMainPhoto = request.getPhotos().stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));
        if (!hasMainPhoto) {
            throw new BusinessException("必须指定一张主照片");
        }

        String status = normalizeStatus(request.getStatus());

        Pet pet = Pet.builder()
                .name(request.getName())
                .type(request.getType())
                .age(request.getAge())
                .breed(request.getBreed())
                .gender(request.getGender() != null ? request.getGender() : "unknown")
                .status(status)
                .description(request.getDescription())
                .healthInfo(request.getHealthInfo())
                .location(request.getLocation())
                .ownerId(request.getOwnerId())
                .weight(request.getWeight())
                .color(request.getColor())
                .neutered(request.getNeutered() != null ? request.getNeutered() : false)
                .vaccinated(request.getVaccinated() != null ? request.getVaccinated() : false)
                .build();

        pet = petRepository.save(pet);

        for (var photoRequest : request.getPhotos()) {
            Photo photo = Photo.builder()
                    .petId(pet.getId())
                    .userId(request.getOwnerId())
                    .imageUrl(photoRequest.getImageUrl())
                    .thumbnailUrl(photoRequest.getThumbnailUrl() != null ? photoRequest.getThumbnailUrl() : photoRequest.getImageUrl())
                    .caption(photoRequest.getCaption() != null ? photoRequest.getCaption() : "")
                    .isMain(photoRequest.getIsMain() != null ? photoRequest.getIsMain() : false)
                    .build();
            photoRepository.save(photo);
        }

        return toPetResponse(pet);
    }

    @Transactional
    public PetResponse updatePet(Long petId, PetCreateRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));

        if (request.getOwnerId() != null && !pet.getOwnerId().equals(request.getOwnerId())) {
            throw new BusinessException("无权编辑该宠物信息");
        }

        if (request.getName() != null) pet.setName(request.getName());
        if (request.getType() != null) pet.setType(request.getType());
        if (request.getAge() != null) pet.setAge(request.getAge());
        if (request.getBreed() != null) pet.setBreed(request.getBreed());
        if (request.getGender() != null) pet.setGender(request.getGender());
        if (request.getStatus() != null) pet.setStatus(normalizeStatus(request.getStatus()));
        if (request.getDescription() != null) pet.setDescription(request.getDescription());
        if (request.getHealthInfo() != null) pet.setHealthInfo(request.getHealthInfo());
        if (request.getLocation() != null) pet.setLocation(request.getLocation());
        if (request.getWeight() != null) pet.setWeight(request.getWeight());
        if (request.getColor() != null) pet.setColor(request.getColor());
        if (request.getNeutered() != null) pet.setNeutered(request.getNeutered());
        if (request.getVaccinated() != null) pet.setVaccinated(request.getVaccinated());

        pet = petRepository.save(pet);

        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            boolean hasMainPhoto = request.getPhotos().stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsMain()));
            if (!hasMainPhoto) {
                throw new BusinessException("必须指定一张主照片");
            }
            
            photoRepository.deleteByPetId(petId);
            
            for (var photoRequest : request.getPhotos()) {
                Photo photo = Photo.builder()
                        .petId(pet.getId())
                        .userId(pet.getOwnerId())
                        .imageUrl(photoRequest.getImageUrl())
                        .thumbnailUrl(photoRequest.getThumbnailUrl() != null ? photoRequest.getThumbnailUrl() : photoRequest.getImageUrl())
                        .caption(photoRequest.getCaption() != null ? photoRequest.getCaption() : "")
                        .isMain(photoRequest.getIsMain() != null ? photoRequest.getIsMain() : false)
                        .build();
                photoRepository.save(photo);
            }
        }

        return toPetResponse(pet);
    }

    @Transactional
    public void deletePet(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));
        
        if (!pet.getOwnerId().equals(userId)) {
            throw new BusinessException("无权删除该宠物");
        }
        
        photoRepository.deleteByPetId(petId);
        petRepository.delete(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId).stream()
                .map(this::toPetResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public int getPetCountByOwner(Long ownerId) {
        return petRepository.countByOwnerId(ownerId);
    }

    public static PetSimpleResponse toPetSimpleResponse(Pet pet) {
        return PetSimpleResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .type(pet.getType())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .gender(pet.getGender())
                .status(pet.getStatus())
                .build();
    }

    private PetResponse toPetResponse(Pet pet) {
        List<Photo> photos = photoRepository.findByPetId(pet.getId());
        Photo mainPhoto = photoRepository.findMainPhotoByPetId(pet.getId());
        
        return PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .type(pet.getType())
                .age(pet.getAge())
                .breed(pet.getBreed())
                .gender(pet.getGender())
                .status(pet.getStatus())
                .description(pet.getDescription())
                .healthInfo(pet.getHealthInfo())
                .location(pet.getLocation())
                .ownerId(pet.getOwnerId())
                .weight(pet.getWeight())
                .color(pet.getColor())
                .neutered(pet.getNeutered())
                .vaccinated(pet.getVaccinated())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .mainPhoto(mainPhoto != null ? toPhotoResponse(mainPhoto) : null)
                .photos(photos.stream().map(this::toPhotoResponse).toList())
                .owner(userService.getUserSimple(pet.getOwnerId()))
                .build();
    }

    private com.example.app.dto.response.PhotoResponse toPhotoResponse(Photo photo) {
        return com.example.app.dto.response.PhotoResponse.builder()
                .id(photo.getId())
                .petId(photo.getPetId())
                .userId(photo.getUserId())
                .imageUrl(cleanImageUrl(photo.getImageUrl()))
                .thumbnailUrl(cleanImageUrl(photo.getThumbnailUrl()))
                .caption(photo.getCaption())
                .likeCount(photo.getLikeCount())
                .viewCount(photo.getViewCount())
                .isMain(photo.getIsMain())
                .uploadTime(photo.getUploadTime())
                .build();
    }
    
    private String cleanImageUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.replace("http://localhost:5000", "")
                  .replace("http://localhost:8080", "")
                  .replace("https://localhost:5000", "")
                  .replace("https://localhost:8080", "");
    }
    
    private String normalizeStatus(String status) {
        if (status == null) {
            return "待领养";
        }
        return switch (status.toLowerCase()) {
            case "adoption", "available" -> "待领养";
            case "share" -> "仅分享";
            case "adopted" -> "已领养";
            case "reserved" -> "已预定";
            default -> status;
        };
    }
}
