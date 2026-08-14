package org.inventory_tracker.security;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCamsAuthenticationException extends RuntimeException {

    public InvalidCamsAuthenticationException(String message) {
        super(message);
    }

    public InvalidCamsAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
