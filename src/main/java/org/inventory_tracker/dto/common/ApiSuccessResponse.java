package org.inventory_tracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiSuccessResponse<T> {

    private LocalDateTime timestamp;

    private Integer status;

    private String message;

    private T data;
}
