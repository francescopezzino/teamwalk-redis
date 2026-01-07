package com.fp.teamwalk.exception;

import org.springframework.data.redis.RedisConnectionFailureException;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", NOT_FOUND.value());
        body.put("error", NOT_FOUND.getReasonPhrase());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", BAD_REQUEST.value());
        body.put("error", BAD_REQUEST.getReasonPhrase());
        body.put("message", "Invalid input: " + ex.getMessage());

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    // Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", INTERNAL_SERVER_ERROR.value());
        body.put("error", INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", "An unexpected error occurred");

        return new ResponseEntity<>(body, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ProblemDetail handleRedisConnectionFailure(RedisConnectionFailureException ex) {
        // 2026 Standard: Use ProblemDetail for consistent API error responses
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                SERVICE_UNAVAILABLE,
                "The cache service is currently unavailable. Operating in database-only mode."
        );
        problemDetail.setTitle("Cache Connection Failure");
        return problemDetail;
    }
}