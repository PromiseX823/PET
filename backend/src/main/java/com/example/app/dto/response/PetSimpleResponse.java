
package com.example.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetSimpleResponse {

    private Long id;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String gender;
    private String status;
}
