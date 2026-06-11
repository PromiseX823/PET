
package com.example.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequest {

    private Long petId;

    private Long userId;

    private String imageUrl;

    private String thumbnailUrl;

    private String caption;

    private Boolean isMain;
}
