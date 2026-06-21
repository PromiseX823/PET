
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.PhotoResponse;
import com.example.app.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PhotoResponse>>> getPhotos(
            @RequestParam(defaultValue = "upload_time") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_order,
            @RequestParam(required = false) Integer limit) {
        List<PhotoResponse> result = photoService.getPhotos(sort_by, sort_order, limit);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<PhotoResponse>>> getHotPhotos(
            @RequestParam(defaultValue = "3") Integer limit) {
        List<PhotoResponse> result = photoService.getHotPhotos(limit);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{photo_id}")
    public ResponseEntity<ApiResponse<PhotoResponse>> getPhoto(@PathVariable("photo_id") Long photoId) {
        PhotoResponse result = photoService.getPhoto(photoId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PhotoResponse>> addPhoto(
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        String imageUrl = (String) request.get("image_url");
        String thumbnailUrl = (String) request.get("thumbnail_url");
        String caption = (String) request.get("caption");
        Boolean isMain = request.get("is_main") != null ? (Boolean) request.get("is_main") : false;
        Long petId = request.get("pet_id") != null ? ((Number) request.get("pet_id")).longValue() : null;

        PhotoResponse result = photoService.createPhoto(userId, imageUrl, thumbnailUrl, caption, isMain, petId);
        return ResponseEntity.ok(ApiResponse.success("上传照片成功", result));
    }

    @PutMapping("/{photo_id}")
    public ResponseEntity<ApiResponse<PhotoResponse>> updatePhoto(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        String caption = (String) request.get("caption");
        Boolean isMain = (Boolean) request.get("is_main");

        PhotoResponse result = photoService.updatePhoto(photoId, caption, isMain);
        return ResponseEntity.ok(ApiResponse.success("照片信息更新成功", result));
    }

    @DeleteMapping("/{photo_id}")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable("photo_id") Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.ok(ApiResponse.success("照片删除成功", null));
    }

    @PostMapping("/{photo_id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likePhoto(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        photoService.likePhoto(photoId, userId);
        
        int likeCount = photoService.getPhotoLikeCount(photoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "点赞成功");
        result.put("like_count", likeCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{photo_id}/unlike")
    public ResponseEntity<ApiResponse<Map<String, Object>>> unlikePhoto(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        photoService.unlikePhoto(photoId, userId);
        
        int likeCount = photoService.getPhotoLikeCount(photoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "取消点赞成功");
        result.put("like_count", likeCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{photo_id}/like-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikeStatus(
            @PathVariable("photo_id") Long photoId,
            @RequestParam Long user_id) {
        boolean isLiked = photoService.isPhotoLiked(photoId, user_id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("is_liked", isLiked);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{photo_id}/collect")
    public ResponseEntity<ApiResponse<Map<String, Object>>> collectPhoto(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        photoService.collectPhoto(photoId, userId);
        
        int collectionCount = photoService.getPhotoCollectionCount(photoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "收藏成功");
        result.put("collection_count", collectionCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{photo_id}/uncollect")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uncollectPhoto(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        photoService.uncollectPhoto(photoId, userId);
        
        int collectionCount = photoService.getPhotoCollectionCount(photoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "取消收藏成功");
        result.put("collection_count", collectionCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{photo_id}/collection-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCollectionStatus(
            @PathVariable("photo_id") Long photoId,
            @RequestParam Long user_id) {
        boolean isCollected = photoService.isPhotoCollected(photoId, user_id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("is_collected", isCollected);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
