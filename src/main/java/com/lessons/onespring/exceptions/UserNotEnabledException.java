package com.lessons.onespring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class UserNotEnabledException extends RuntimeException {

    public UserNotEnabledException() {
        super("User is not enabled");
    }

}
