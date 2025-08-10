package com.itmuch.contentcenter.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({SecurityException.class})
    public ResponseEntity<ErrorBody> handleException(SecurityException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<ErrorBody>(
                ErrorBody.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message(ex.getMessage())
                        .build(),
          HttpStatus.UNAUTHORIZED
        );
    }

}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
class ErrorBody {

    private int status;

    private String message;

}