package br.goes.luis.application.core.infrastructure.exception.domain;

import br.goes.luis.application.core.infrastructure.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DomainException extends ApplicationException {
    public DomainException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}