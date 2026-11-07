package com.aivastudio.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> errors) {
}