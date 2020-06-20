package com.lessons.onespring.controllers.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class ApiErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp;
    private int code;
    private String message;
    private String path;
    private Map<String, Set<String>> errors = new HashMap<>();

    public ApiErrorResponse(Date timestamp, int code, String message, String path, Map<String, Set<String>> errors) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public ApiErrorResponse(Date timestamp, int code, String message, String path) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.path = path;
    }
}

