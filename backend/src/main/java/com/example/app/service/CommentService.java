
package com.example.app.service;

import com.example.app.dto.response.CommentResponse;
import com.example.app.entity.Comment;
import com.example.app.entity.Photo;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.PhotoRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<CommentResponse> getPhotoComments(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        return commentRepository.findByPhotoIdAndIsDeletedFalse(photoId).stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional
    public CommentResponse createComment(Long photoId, Long userId, String content, Long parentId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException("照片不存在"));

        Comment comment = Comment.builder()
                .photoId(photoId)
                .userId(userId)
                .content(content)
                .parentId(parentId)
                .build();

        comment = commentRepository.save(comment);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !photo.getUserId().equals(userId)) {
            notificationService.createNotification(
                    photo.getUserId(),
                    "有人评论了你的照片",
                    user.getUsername() + " 评论了你的照片: " + (content.length() > 20 ? content.substring(0, 20) + "..." : content),
                    "comment",
                    photoId
            );
        }

        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId).orElse(null);
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                notificationService.createNotification(
                        parentComment.getUserId(),
                        "有人回复了你的评论",
                        user.getUsername() + " 回复了你的评论: " + (content.length() > 20 ? content.substring(0, 20) + "..." : content),
                        "comment",
                        parentId
                );
            }
        }

        return toCommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));

        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    private CommentResponse toCommentResponse(Comment comment) {
        User user = userRepository.findById(comment.getUserId()).orElse(null);
        User parentUser = null;
        
        if (comment.getParentId() != null) {
            Comment parentComment = commentRepository.findById(comment.getParentId()).orElse(null);
            if (parentComment != null) {
                parentUser = userRepository.findById(parentComment.getUserId()).orElse(null);
            }
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .photoId(comment.getPhotoId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCommentTime())
                .user(user != null ? com.example.app.dto.response.UserSimpleResponse.builder()
                        .id(user.getId()).username(user.getUsername()).avatarUrl(cleanImageUrl(user.getAvatarUrl())).build() : null)
                .parentUser(parentUser != null ? com.example.app.dto.response.UserSimpleResponse.builder()
                        .id(parentUser.getId()).username(parentUser.getUsername()).build() : null)
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<CommentResponse> getUserComments(Long userId) {
        return commentRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(this::toCommentResponse)
                .toList();
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
}
