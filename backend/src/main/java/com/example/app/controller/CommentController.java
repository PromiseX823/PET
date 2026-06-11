
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.CommentResponse;
import com.example.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/photos/{photo_id}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getPhotoComments(@PathVariable("photo_id") Long photoId) {
        List<CommentResponse> result = commentService.getPhotoComments(photoId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/photos/{photo_id}")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable("photo_id") Long photoId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        String content = (String) request.get("content");
        Long parentId = request.get("parent_id") != null ? ((Number) request.get("parent_id")).longValue() : null;

        CommentResponse result = commentService.createComment(photoId, userId, content, parentId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }
}
