
package com.example.app.repository;

import com.example.app.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByPetId(Long petId);
    
    List<Favorite> findByUserId(Long userId);
    
    Optional<Favorite> findByPetIdAndUserId(Long petId, Long userId);
    
    boolean existsByPetIdAndUserId(Long petId, Long userId);
    
    void deleteByPetIdAndUserId(Long petId, Long userId);
}
