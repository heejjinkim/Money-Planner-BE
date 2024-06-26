package com.umc5th.muffler.global.response;

import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CustomException;
import com.umc5th.muffler.global.response.exception.FeignException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage,
                            (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error("Request is incomplete.", errors));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e) {
        log.error("Error occurs {}", e.toString());
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Response.error(message));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException e) {
        HttpStatus status = e.getStatus();
        String url = e.getUrl();
        String errorResult = e.getErrorResult();
        String message = String.format("status: %s, url: %s", status.toString(), url);
        message = message + ", " + errorResult;
        log.error("Feign Error occur. {}", message);
        return ResponseEntity.status(status)
                .body(Response.error(message));
    }
}
