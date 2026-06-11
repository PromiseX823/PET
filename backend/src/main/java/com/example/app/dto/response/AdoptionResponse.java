
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
public class AdoptionResponse {

    private Long id;
    private Long petId;
    private Long applicantId;
    private String status;
    private LocalDateTime applyTime;
    private LocalDateTime reviewTime;
    private String applicantNote;
    private String adminNote;
    private PetSimpleResponse pet;
    private UserSimpleResponse user;
}
