package com.lessons.onespring.controllers.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lessons.onespring.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static com.lessons.onespring.constants.Constant.DOB_FORMATTER;

@Getter
@Setter
public class UserUpdateSelfRequest {

    @NotBlank
    @Size(min = 2, max = 70)
    private String name;

    private String dob;

    @AssertTrue(message = "valid date format: dd-mm-yyyy")
    private boolean isDob() {
        if (dob != null) {
            return DateUtils.isValidDate(dob);
        }
        return true;
    }
}
