package com.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Long relatedId;
    private LocalDateTime createdAt;
}