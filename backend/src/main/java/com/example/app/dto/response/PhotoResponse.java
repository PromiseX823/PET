
package com.example.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponse {

    private Long id;
    
    @JsonProperty("pet_id")
    private Long petId;
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    
    private String caption;
    
    @JsonProperty("like_count")
    private Integer likeCount;
    
    @JsonProperty("view_count")
    private Integer viewCount;
    
    @JsonProperty("comment_count")
    private Integer commentCount;
    
    @JsonProperty("collection_count")
    private Integer collectionCount;
    
    @JsonProperty("is_main")
    private Boolean isMain;
    
    @JsonProperty("upload_time")
    private LocalDateTime uploadTime;
    
    private PetSimpleResponse pet;
    private UserSimpleResponse user;
}
