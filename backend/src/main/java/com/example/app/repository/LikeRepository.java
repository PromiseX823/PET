
package com.example.app.repository;

import com.example.app.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    List<Like> findByPhotoId(Long photoId);
    
    List<Like> findByUserId(Long userId);
    
    Optional<Like> findByPhotoIdAndUserId(Long photoId, Long userId);
    
    boolean existsByPhotoIdAndUserId(Long photoId, Long userId);
    
    int countByPhotoId(Long photoId);
    
    void deleteByPhotoIdAndUserId(Long photoId, Long userId);
    
    void deleteByPhotoId(Long photoId);
}
