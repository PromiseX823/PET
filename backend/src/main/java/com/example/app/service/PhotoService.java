
package com.example.app.service;

import com.example.app.dto.response.PhotoResponse;
import com.example.app.entity.Like;
import com.example.app.entity.Photo;
import com.example.app.entity.PhotoCollection;
import com.example.app.entity.Pet;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.LikeRepository;
import com.example.app.repository.PetRepository;
import com.example.app.repository.PhotoCollectionRepository;
import com.example.app.repository.PhotoRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PhotoCollectionRepository photoCollectionRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotos(String sortBy, String sortOrder, Integer limit) {
        String camelCaseSortBy = convertToCamelCase(sortBy);
        var sort = sortOrder.equalsIgnoreCase("desc") 
                ? org.springframework.data.domain.Sort.by(camelCaseSortBy).descending()
                : org.springframework.data.domain.Sort.by(camelCaseSortBy).ascending();
        
        var pageable = PageRequest.of(0, limit != null ? limit : Integer.MAX_VALUE, sort);
        return photoRepository.findAll(pageable).stream().map(this::toPhotoResponse).toList();
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

    @Transactional(readOnly = true)
    public List<PhotoResponse> getHotPhotos(Integer limit) {
        var pageable = PageRequest.of(0, limit != null ? limit : 3);
        return photoRepository.findTopHotPhotos(pageable).stream().map(this::toPhotoResponse).toList();
    }

    @Transactional
    public PhotoResponse getPhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));
        
        photoRepository.incrementViewCount(photoId);
        photo = photoRepository.findById(photoId).orElseThrow();
        
        return toPhotoResponse(photo);
    }

    @Transactional
    public PhotoResponse createPhoto(Long userId, String imageUrl, String thumbnailUrl, 
                                     String caption, Boolean isMain, Long petId) {
        if (petId != null) {
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new BusinessException("宠物不存在"));
            if (!pet.getOwnerId().equals(userId)) {
                throw new BusinessException("无权为该宠物上传照片");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Photo photo = Photo.builder()
                .petId(petId)
                .userId(userId)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl != null ? thumbnailUrl : imageUrl)
                .caption(caption != null ? caption : "")
                .isMain(isMain != null ? isMain : false)
                .build();

        photo = photoRepository.save(photo);
        return toPhotoResponse(photo);
    }

    @Transactional
    public PhotoResponse updatePhoto(Long photoId, String caption, Boolean isMain) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        if (caption != null) photo.setCaption(caption);
        if (isMain != null) photo.setIsMain(isMain);

        photo = photoRepository.save(photo);
        return toPhotoResponse(photo);
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));
        
        commentRepository.deleteByPhotoId(photoId);
        likeRepository.deleteByPhotoId(photoId);
        photoCollectionRepository.deleteByPhotoId(photoId);
        photoRepository.delete(photo);
    }

    @Transactional
    public void likePhoto(Long photoId, Long userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        if (likeRepository.existsByPhotoIdAndUserId(photoId, userId)) {
            throw new BusinessException("已经点赞过了");
        }

        Like like = new Like();
        like.setPhotoId(photoId);
        like.setUserId(userId);
        likeRepository.save(like);

        int likeCount = likeRepository.countByPhotoId(photoId);
        photo.setLikeCount(likeCount);
        photoRepository.save(photo);

        if (!photo.getUserId().equals(userId)) {
            try {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    notificationService.createNotification(
                            photo.getUserId(),
                            "有人点赞了你的照片",
                            user.getUsername() + " 点赞了你的照片",
                            "like",
                            photoId
                    );
                }
            } catch (Exception e) {
                log.warn("发送点赞通知失败，可能是消息队列不可用", e);
            }
        }
    }

    @Transactional
    public void unlikePhoto(Long photoId, Long userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        likeRepository.deleteByPhotoIdAndUserId(photoId, userId);
        
        int likeCount = likeRepository.countByPhotoId(photoId);
        photo.setLikeCount(likeCount);
        photoRepository.save(photo);
    }

    @Transactional(readOnly = true)
    public boolean isPhotoLiked(Long photoId, Long userId) {
        return likeRepository.existsByPhotoIdAndUserId(photoId, userId);
    }

    @Transactional
    public void collectPhoto(Long photoId, Long userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        if (photoCollectionRepository.existsByPhotoIdAndUserId(photoId, userId)) {
            throw new BusinessException("已经收藏过了");
        }

        PhotoCollection collection = new PhotoCollection();
        collection.setPhotoId(photoId);
        collection.setUserId(userId);
        photoCollectionRepository.save(collection);

        if (!photo.getUserId().equals(userId)) {
            try {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    notificationService.createNotification(
                            photo.getUserId(),
                            "有人收藏了你的照片",
                            user.getUsername() + " 收藏了你的照片",
                            "collection",
                            photoId
                    );
                }
            } catch (Exception e) {
                log.warn("发送收藏通知失败，可能是消息队列不可用", e);
            }
        }
    }

    @Transactional
    public void uncollectPhoto(Long photoId, Long userId) {
        photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        photoCollectionRepository.deleteByPhotoIdAndUserId(photoId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isPhotoCollected(Long photoId, Long userId) {
        return photoCollectionRepository.existsByPhotoIdAndUserId(photoId, userId);
    }

    @Transactional(readOnly = true)
    public int getPhotoLikeCount(Long photoId) {
        return likeRepository.countByPhotoId(photoId);
    }

    @Transactional(readOnly = true)
    public int getPhotoCollectionCount(Long photoId) {
        return photoCollectionRepository.countByPhotoId(photoId);
    }

    @Transactional(readOnly = true)
    public int getPhotoCountByUser(Long userId) {
        return photoRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotosByUser(Long userId) {
        return photoRepository.findByUserId(userId).stream()
                .map(this::toPhotoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> getLikedPhotosByUser(Long userId) {
        List<Like> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(like -> photoRepository.findById(like.getPhotoId()).orElse(null))
                .filter(photo -> photo != null)
                .map(this::toPhotoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> getCollectedPhotosByUser(Long userId) {
        List<PhotoCollection> collections = photoCollectionRepository.findByUserId(userId);
        return collections.stream()
                .map(collection -> photoRepository.findById(collection.getPhotoId()).orElse(null))
                .filter(photo -> photo != null)
                .map(this::toPhotoResponse)
                .toList();
    }

    private PhotoResponse toPhotoResponse(Photo photo) {
        Pet pet = photo.getPetId() != null ? petRepository.findById(photo.getPetId()).orElse(null) : null;
        User user = userRepository.findById(photo.getUserId()).orElse(null);
        int commentCount = commentRepository.countByPhotoIdAndIsDeletedFalse(photo.getId());
        int collectionCount = photoCollectionRepository.countByPhotoId(photo.getId());

        return PhotoResponse.builder()
                .id(photo.getId())
                .petId(photo.getPetId())
                .userId(photo.getUserId())
                .imageUrl(cleanImageUrl(photo.getImageUrl()))
                .thumbnailUrl(cleanImageUrl(photo.getThumbnailUrl()))
                .caption(photo.getCaption())
                .likeCount(photo.getLikeCount())
                .viewCount(photo.getViewCount())
                .commentCount(commentCount)
                .collectionCount(collectionCount)
                .isMain(photo.getIsMain())
                .uploadTime(photo.getUploadTime())
                .pet(pet != null ? com.example.app.dto.response.PetSimpleResponse.builder()
                        .id(pet.getId()).name(pet.getName()).type(pet.getType()).build() : null)
                .user(user != null ? com.example.app.dto.response.UserSimpleResponse.builder()
                        .id(user.getId()).username(user.getUsername()).avatarUrl(cleanImageUrl(user.getAvatarUrl())).build() : null)
                .build();
    }
    
    private String cleanImageUrl(String url) {
        if (url == null) {
            return null;
        }
        // 移除旧的主机和端口，保留相对路径
        return url.replace("http://localhost:5000", "")
                  .replace("http://localhost:8080", "")
                  .replace("https://localhost:5000", "")
                  .replace("https://localhost:8080", "");
    }
}
