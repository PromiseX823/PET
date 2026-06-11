
package com.example.app.repository;

import com.example.app.entity.Adoption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    
    List<Adoption> findByPetId(Long petId);
    
    List<Adoption> findByApplicantId(Long applicantId);
    
    List<Adoption> findByStatus(String status);
    
    Page<Adoption> findByStatus(String status, Pageable pageable);
    
    Page<Adoption> findByApplicantId(Long applicantId, Pageable pageable);
    
    Page<Adoption> findByPetId(Long petId, Pageable pageable);
    
    @Query("SELECT a FROM Adoption a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:applicantId IS NULL OR a.applicantId = :applicantId) AND " +
           "(:petId IS NULL OR a.petId = :petId)")
    Page<Adoption> findByFilters(
            @Param("status") String status,
            @Param("applicantId") Long applicantId,
            @Param("petId") Long petId,
            Pageable pageable
    );
    
    @Query("SELECT a FROM Adoption a WHERE a.petId = :petId AND a.status IN ('pending', '待审核') AND a.id != :excludeId")
    List<Adoption> findOtherPendingAdoptions(@Param("petId") Long petId, @Param("excludeId") Long excludeId);
    
    @Query("SELECT COUNT(a) > 0 FROM Adoption a WHERE " +
           "a.petId = :petId AND a.applicantId = :applicantId AND " +
           "a.status NOT IN ('rejected', '已拒绝')")
    boolean existsActiveAdoption(@Param("petId") Long petId, @Param("applicantId") Long applicantId);
}
