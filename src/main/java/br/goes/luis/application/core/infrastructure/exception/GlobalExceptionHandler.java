package br.goes.luis.application.core.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleApplicationException(ApplicationException ex, HttpServletRequest request) {
        var problemDetail = buildProblemDetail(ex.getHttpStatus(), ex.getMessage(), ex.getTimestamp(), request);
        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandledException(HttpServletRequest request) {
        var problemDetail = buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", Instant.now(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private ProblemDetail buildProblemDetail(HttpStatus httpStatus, String message, Instant timestamp, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setDetail(message);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("http-method", request.getMethod());
        problemDetail.setProperty("agent", request.getHeader("User-Agent"));
        problemDetail.setProperty("timestamp", timestamp);
        return problemDetail;
    }

}