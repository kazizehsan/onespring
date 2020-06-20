package com.lessons.onespring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private Map<String, Set<String>> errors = new HashMap<>();

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public void setErrors(Map<String, Set<String>> errors) {
        this.errors = errors;
    }

    public Map<String, Set<String>> getErrors() {
        return errors;
    }

    public void setFieldError(String fieldName, String fieldError){
        if (this.errors.containsKey(fieldName)){
            Set<String> values = this.errors.get(fieldName);
            values.add(fieldError);
            this.errors.replace(fieldName, values);
        } else {
            Set<String> values = new HashSet<>();
            values.add(fieldError);
            this.errors.put(fieldName, values);
        }
    }
}
