
package com.example.app.service;

import com.example.app.entity.Favorite;
import com.example.app.entity.Pet;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.PetRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void favoritePet(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (favoriteRepository.existsByPetIdAndUserId(petId, userId)) {
            throw new BusinessException("已经收藏过了");
        }

        Favorite favorite = Favorite.builder()
                .petId(petId)
                .userId(userId)
                .build();

        favoriteRepository.save(favorite);

        if (!pet.getOwnerId().equals(userId)) {
            notificationService.createNotification(
                    pet.getOwnerId(),
                    "有人收藏了你的宠物",
                    user.getUsername() + " 收藏了你的宠物 " + pet.getName(),
                    "favorite",
                    petId
            );
        }
    }

    @Transactional
    public void unfavoritePet(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException("宠物不存在"));

        favoriteRepository.deleteByPetIdAndUserId(petId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isPetFavorited(Long petId, Long userId) {
        return favoriteRepository.existsByPetIdAndUserId(petId, userId);
    }

    @Transactional(readOnly = true)
    public List<Pet> getUserFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Long> petIds = favorites.stream()
                .map(Favorite::getPetId)
                .collect(Collectors.toList());
        
        if (petIds.isEmpty()) {
            return List.of();
        }
        
        return petRepository.findAllById(petIds);
    }
}
