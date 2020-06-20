package com.lessons.onespring.controllers.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lessons.onespring.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class UserUpdateByAdminRequest {
    @NotBlank
    @Size(min = 2, max = 70)
    private String name;

    private String dob;

    @NotNull
    private Boolean enabled;

    @Size(min = 8, max = 72)
    private String password;

    @NotEmpty
    private Set<Long> privileges;

    @AssertTrue(message = "valid date format: dd-mm-yyyy")
    private boolean isDob() {
        if (dob != null) {
            return DateUtils.isValidDate(dob);
        }
        return true;
    }
}
