
package com.example.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PetResponse {

    private Long id;
    private String name;
    private String type;
    private Integer age;
    private String breed;
    private String gender;
    private String status;
    private String description;
    
    @JsonProperty("health_info")
    private String healthInfo;
    
    private String location;
    
    @JsonProperty("owner_id")
    private Long ownerId;
    
    private Integer weight;
    private String color;
    private Boolean neutered;
    private Boolean vaccinated;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("main_photo")
    private PhotoResponse mainPhoto;
    
    private List<PhotoResponse> photos;
    private UserSimpleResponse owner;
}
