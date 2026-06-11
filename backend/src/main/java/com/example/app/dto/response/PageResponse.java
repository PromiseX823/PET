
package com.example.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> pets;
    private Long total;
    private Integer page;
    private Integer page_size;
    private Integer total_pages;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .pets(page.getContent())
                .total(page.getTotalElements())
                .page(page.getNumber() + 1)
                .page_size(page.getSize())
                .total_pages(page.getTotalPages())
                .build();
    }
}
