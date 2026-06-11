
package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @Column(length = 255)
    private String caption;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @CreationTimestamp
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = true;

    @Column(name = "is_main")
    @Builder.Default
    private Boolean isMain = false;
}
