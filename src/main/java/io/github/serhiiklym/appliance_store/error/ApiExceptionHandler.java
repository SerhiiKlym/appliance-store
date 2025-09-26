package io.github.serhiiklym.appliance_store.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ApiExceptionHandler {

    //TODO migrate to records
    @Getter
    public static class ApiError{
        private final Instant timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ApiError(Instant timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(DuplicateManufacturerNameException.class)
    public ResponseEntity<ApiError> handleConflict(DuplicateManufacturerNameException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
               Instant.now(),
               HttpStatus.CONFLICT.value(),
               "Conflict",
               ex.getMessage(),
               request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
}
