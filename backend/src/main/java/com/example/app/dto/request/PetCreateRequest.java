
package com.example.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateRequest {

    @NotBlank(message = "宠物名称不能为空")
    private String name;

    @NotBlank(message = "宠物类型不能为空")
    private String type;

    private Integer age;

    private String breed;

    private String gender;

    private String status;

    private String description;

    private String healthInfo;

    private String location;

    @NotNull(message = "所有者ID不能为空")
    private Long ownerId;

    private Integer weight;

    private String color;

    private Boolean neutered;

    private Boolean vaccinated;

    private List<PhotoRequest> photos;
}
