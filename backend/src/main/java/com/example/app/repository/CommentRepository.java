
package com.example.app.repository;

import com.example.app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPhotoIdAndIsDeletedFalse(Long photoId);
    
    List<Comment> findByUserIdAndIsDeletedFalse(Long userId);
    
    List<Comment> findByParentIdAndIsDeletedFalse(Long parentId);
    
    @Query("SELECT c FROM Comment c WHERE c.photoId IN :photoIds AND c.isDeleted = false ORDER BY c.commentTime DESC")
    List<Comment> findByPhotoIds(@Param("photoIds") List<Long> photoIds);
    
    int countByPhotoIdAndIsDeletedFalse(Long photoId);
    
    void deleteByPhotoId(Long photoId);
    
    void deleteByParentId(Long parentId);
    
    void deleteByIdAndIsDeletedFalse(Long id);
}
