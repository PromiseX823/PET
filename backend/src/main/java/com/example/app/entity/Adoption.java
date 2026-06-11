
package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @CreationTimestamp
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(length = 10)
    @Builder.Default
    private String status = "pending";

    @Column(name = "applicant_note", columnDefinition = "TEXT")
    private String applicantNote;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "contact_method", length = 50)
    private String contactMethod;

    @Column(name = "interview_time")
    private LocalDateTime interviewTime;
}
