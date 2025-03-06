package dev.da0hn.email.management.system.infrastructure.web.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        var errors = exception.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> ErrorResponse.Error.builder()
                .message(error.getDefaultMessage())
                .build()
            )
            .toList();

        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message("Invalid request parameters")
            .path(request.getRequestURI())
            .errors(errors)
            .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(
        EntityNotFoundException exception,
        HttpServletRequest request
    ) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message("Resource not found")
            .path(request.getRequestURI())
            .errors(List.of(
                ErrorResponse.Error.builder()
                    .message(exception.getMessage())
                    .build()
            ))
            .build();
    }
}
