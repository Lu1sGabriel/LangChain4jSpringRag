package br.goes.luis.application.core.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;
    private final Instant timestamp;

    protected ApplicationException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timestamp = Instant.now();
    }

}