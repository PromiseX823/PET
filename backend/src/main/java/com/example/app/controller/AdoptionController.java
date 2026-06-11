
package com.example.app.controller;

import com.example.app.dto.request.AdoptionRequest;
import com.example.app.dto.response.AdoptionResponse;
import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.PageResponse;
import com.example.app.service.AdoptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdoptionResponse>>> getAdoptions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long user_id,
            @RequestParam(required = false) Long pet_id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        PageResponse<AdoptionResponse> result = adoptionService.getAdoptions(status, user_id, pet_id, page, page_size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{adoption_id}")
    public ResponseEntity<ApiResponse<AdoptionResponse>> getAdoption(@PathVariable("adoption_id") Long adoptionId) {
        AdoptionResponse result = adoptionService.getAdoption(adoptionId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdoptionResponse>> createAdoption(@Valid @RequestBody AdoptionRequest request) {
        AdoptionResponse result = adoptionService.createAdoption(request);
        return ResponseEntity.ok(ApiResponse.success("领养申请创建成功", result));
    }

    @PostMapping("/{adoption_id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveAdoption(
            @PathVariable("adoption_id") Long adoptionId,
            @RequestBody Map<String, Object> request) {
        Long reviewerId = ((Number) request.get("reviewer_id")).longValue();
        String reviewComment = (String) request.get("review_comment");
        adoptionService.approveAdoption(adoptionId, reviewerId, reviewComment);
        return ResponseEntity.ok(ApiResponse.success("领养申请已批准", null));
    }

    @PostMapping("/{adoption_id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectAdoption(
            @PathVariable("adoption_id") Long adoptionId,
            @RequestBody Map<String, Object> request) {
        Long reviewerId = ((Number) request.get("reviewer_id")).longValue();
        String reviewComment = (String) request.get("review_comment");
        adoptionService.rejectAdoption(adoptionId, reviewerId, reviewComment);
        return ResponseEntity.ok(ApiResponse.success("领养申请已拒绝", null));
    }

    @PostMapping("/{adoption_id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelAdoption(@PathVariable("adoption_id") Long adoptionId) {
        adoptionService.cancelAdoption(adoptionId);
        return ResponseEntity.ok(ApiResponse.success("领养申请已取消", null));
    }

    @PostMapping("/{adoption_id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeAdoption(@PathVariable("adoption_id") Long adoptionId) {
        adoptionService.completeAdoption(adoptionId);
        return ResponseEntity.ok(ApiResponse.success("领养已完成", null));
    }

    @GetMapping("/pets/{pet_id}")
    public ResponseEntity<ApiResponse<List<AdoptionResponse>>> getPetAdoptions(@PathVariable("pet_id") Long petId) {
        List<AdoptionResponse> result = adoptionService.getPetAdoptions(petId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<List<AdoptionResponse>>> getUserAdoptions(@PathVariable("user_id") Long userId) {
        List<AdoptionResponse> result = adoptionService.getUserAdoptions(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
