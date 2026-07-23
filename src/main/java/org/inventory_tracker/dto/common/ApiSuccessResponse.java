package org.inventory_tracker.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ApiSuccessResponse<T> {

    private LocalDateTime timestamp;

    private Integer status;

    private String message;

    private Integer recordCount;

    private T data;

    public ApiSuccessResponse(LocalDateTime timestamp, int status, String message, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
        this.recordCount = null;
    }

    public ApiSuccessResponse(LocalDateTime timestamp, int status, String message, Integer recordCount, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
        this.recordCount = recordCount;
    }
}
