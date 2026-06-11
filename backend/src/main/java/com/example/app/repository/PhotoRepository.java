
package com.example.app.repository;

import com.example.app.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    
    List<Photo> findByPetId(Long petId);
    
    List<Photo> findByUserId(Long userId);
    
    Optional<Photo> findByPetIdAndIsMainTrue(Long petId);
    
    @Query("SELECT p FROM Photo p WHERE p.petId = :petId AND p.isMain = true")
    Photo findMainPhotoByPetId(@Param("petId") Long petId);
    
    @Modifying
    @Query("UPDATE Photo p SET p.likeCount = p.likeCount + 1 WHERE p.id = :photoId")
    void incrementLikeCount(@Param("photoId") Long photoId);
    
    @Modifying
    @Query("UPDATE Photo p SET p.viewCount = p.viewCount + 1 WHERE p.id = :photoId")
    void incrementViewCount(@Param("photoId") Long photoId);
    
    void deleteByPetId(Long petId);
    
    @Query("SELECT p FROM Photo p ORDER BY p.likeCount DESC")
    List<Photo> findTopHotPhotos(org.springframework.data.domain.Pageable pageable);
}
