package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.errors.ErrorResponseDTO;
import fr.isaaconnect.sweb.dto.errors.FieldErrorDTO;
import fr.isaaconnect.sweb.dto.errors.FieldValidationErrorDTO;
import fr.isaaconnect.sweb.exceptions.ConflictException;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handle(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDTO.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .build()
            );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handle(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponseDTO.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .build()
            );
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        @Nullable Object body,
        HttpHeaders headers,
        HttpStatusCode statusCode,
        WebRequest request
    ) {
        String message = ex.getMessage();

        if (body instanceof ProblemDetail problem)
        {
            if (problem.getDetail() != null) message = problem.getDetail();
        }

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .code(statusCode.value())
            .message(message)
            .build();

        return super.handleExceptionInternal(ex, errorResponse, headers, statusCode, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        List<FieldErrorDTO> fieldErrors = ex.getBindingResult()
            .getFieldErrors().stream()
            .map(FieldErrorDTO::fromFieldError)
            .toList();

        FieldValidationErrorDTO errorResponse = FieldValidationErrorDTO.builder()
            .code(status.value())
            .message("Fields provided are wrong")
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.status(status).headers(headers).body(errorResponse);
    }
}
