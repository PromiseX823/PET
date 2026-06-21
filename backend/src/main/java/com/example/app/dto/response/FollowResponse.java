package com.example.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {
    private Long id;
    private Long followerId;
    private String followerUsername;
    private String followerAvatar;
    private Long followingId;
    private String followingUsername;
    private String followingAvatar;
    private LocalDateTime createdAt;
}
