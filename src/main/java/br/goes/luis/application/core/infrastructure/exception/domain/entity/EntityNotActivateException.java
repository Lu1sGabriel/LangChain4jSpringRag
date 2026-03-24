package br.goes.luis.application.core.infrastructure.exception.domain.entity;

import br.goes.luis.application.core.infrastructure.exception.domain.DomainException;
import org.springframework.http.HttpStatus;

public class EntityNotActivateException extends DomainException {
    public EntityNotActivateException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}