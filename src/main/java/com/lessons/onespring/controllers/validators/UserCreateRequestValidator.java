package com.lessons.onespring.controllers.validators;

import com.lessons.onespring.controllers.requests.UserCreateRequest;
import com.lessons.onespring.utils.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserCreateRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return UserCreateRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UserCreateRequest userCreateRequest = (UserCreateRequest) obj;

        ValidatorUtils.validateEmail(errors, "email", true);
        ValidatorUtils.validateLength(errors, "password", true, 8, 72);
        ValidatorUtils.validateLength(errors, "name", true, 2, 70);
        ValidatorUtils.validateDate(errors, "dob", false);
        ValidatorUtils.validateCommaSeparatedDigits(errors, "privileges", true);
        ValidatorUtils.validateImage(errors, "photo", false);
    }
}
