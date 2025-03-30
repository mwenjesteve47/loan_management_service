package com.example.loanmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginateObjectResponse<T> {
    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static PaginateObjectResponse<?> toResponse(Page entity){
        return PaginateObjectResponse
                .builder()
                .content(entity.getContent())
                .pageNo(entity.getNumber())
                .totalElements(entity.getTotalElements())
                .totalPages(entity.getTotalPages())
                .last(entity.isLast())
                .build();
    }
}
