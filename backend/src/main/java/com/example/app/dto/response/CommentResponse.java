
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
public class CommentResponse {

    private Long id;
    private Long photoId;
    private Long userId;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    private UserSimpleResponse user;
    private UserSimpleResponse parentUser;
}
