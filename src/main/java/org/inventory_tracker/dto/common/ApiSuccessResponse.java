package org.inventory_tracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
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
}
