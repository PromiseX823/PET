
package com.example.app.repository;

import com.example.app.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
    
    List<Pet> findByStatus(String status);
    
    List<Pet> findByType(String type);
    
    List<Pet> findByOwnerId(Long ownerId);
    
    Page<Pet> findByStatus(String status, Pageable pageable);
    
    Page<Pet> findByType(String type, Pageable pageable);
    
    int countByOwnerId(Long ownerId);
}
