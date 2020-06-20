package com.lessons.onespring.controllers.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SetPasswordRequest {
    @NotBlank
    @Size(min = 8, max = 72)
    @JsonProperty("password")
    private String password;

    @NotBlank
    @Size(min = 8, max = 72)
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
