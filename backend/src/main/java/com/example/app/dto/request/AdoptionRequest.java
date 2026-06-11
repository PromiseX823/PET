
package com.example.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionRequest {

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String reason;

    private String phone;

    private String address;
}
