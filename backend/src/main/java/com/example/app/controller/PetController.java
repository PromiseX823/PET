
package com.example.app.controller;

import com.example.app.dto.request.PetCreateRequest;
import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.PageResponse;
import com.example.app.dto.response.PetResponse;
import com.example.app.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PetResponse>>> getPets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "created_at") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_order) {
        
        PageResponse<PetResponse> result = petService.getPets(
                type, status, breed, gender, location, page, page_size, sort_by, sort_order);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{pet_id}")
    public ResponseEntity<ApiResponse<PetResponse>> getPet(@PathVariable("pet_id") Long petId) {
        PetResponse result = petService.getPet(petId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PetResponse>> createPet(@Valid @RequestBody PetCreateRequest request) {
        PetResponse result = petService.createPet(request);
        return ResponseEntity.ok(ApiResponse.success("宠物信息创建成功", result));
    }

    @PutMapping("/{pet_id}")
    public ResponseEntity<ApiResponse<PetResponse>> updatePet(
            @PathVariable("pet_id") Long petId,
            @RequestBody PetCreateRequest request) {
        PetResponse result = petService.updatePet(petId, request);
        return ResponseEntity.ok(ApiResponse.success("宠物信息更新成功", result));
    }

    @DeleteMapping("/{pet_id}")
    public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable("pet_id") Long petId) {
        petService.deletePet(petId);
        return ResponseEntity.ok(ApiResponse.success("宠物删除成功", null));
    }
}
