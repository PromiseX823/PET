
package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String type;

    private Integer age;

    @Column(length = 50)
    private String breed;

    @Column(length = 10)
    @Builder.Default
    private String gender = "unknown";

    @Column(length = 10)
    @Builder.Default
    private String status = "待领养";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "health_info", columnDefinition = "TEXT")
    private String healthInfo;

    @Column(length = 100)
    private String location;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    private Integer weight;

    @Column(length = 50)
    private String color;

    @Builder.Default
    private Boolean neutered = false;

    @Builder.Default
    private Boolean vaccinated = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
