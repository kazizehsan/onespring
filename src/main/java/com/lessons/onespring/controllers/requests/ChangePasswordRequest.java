package com.lessons.onespring.controllers.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank
    @Size(min = 8, max = 72)
    @JsonProperty("old_password")
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    @JsonProperty("confirm_new_password")
    private String confirmNewPassword;

    @AssertTrue(message = "New passwords do not match")
    private boolean isConfirmNewPassword() {
        if (newPassword == null || confirmNewPassword == null) {
            return false;
        }
        return newPassword.equals(confirmNewPassword);
    }
}
