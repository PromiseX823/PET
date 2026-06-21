
package com.example.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private LocalDateTime createdAt;
    private Integer followingCount;
    private Integer followerCount;
    private Integer petCount;
    private Integer photoCount;
    private String description;
    private String area;
    private List<PetResponse> pets;
    private List<PhotoResponse> photos;
    private List<AdoptionResponse> applications;
    private List<CommentResponse> comments;
}
