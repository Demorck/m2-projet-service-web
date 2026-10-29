package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.errors.ErrorResponseDTO;
import fr.isaaconnect.sweb.exceptions.ConflictException;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}
