
package com.example.app.repository;

import com.example.app.entity.PhotoCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoCollectionRepository extends JpaRepository<PhotoCollection, Long> {
    
    List<PhotoCollection> findByPhotoId(Long photoId);
    
    List<PhotoCollection> findByUserId(Long userId);
    
    Optional<PhotoCollection> findByPhotoIdAndUserId(Long photoId, Long userId);
    
    boolean existsByPhotoIdAndUserId(Long photoId, Long userId);
    
    int countByPhotoId(Long photoId);
    
    void deleteByPhotoIdAndUserId(Long photoId, Long userId);
    
    void deleteByPhotoId(Long photoId);
}
