package com.lessons.onespring.utils;

import com.lessons.onespring.exceptions.BadRequestException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ValidatorUtils {

    public static void throwValidationException(BindingResult result) throws BadRequestException {
        final List<FieldError> fieldErrors = result.getFieldErrors();
        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(
                Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
                )
        );
        BadRequestException badRequestException = new BadRequestException("Validation failed");
        badRequestException.setErrors(errorsMap);
        throw badRequestException;
    }

    public static void validateEmail(Errors errors, String field, boolean required) {
        String value = (String) errors.getFieldValue(field);
        if (value == null) {
            if (required) {
                errors.rejectValue(field, "required", "this is required");
            }
            return;
        }
        if (
                !(Pattern.matches("^[A-Za-z0-9@._%+-]{6,254}$", value)
                        && Pattern.matches("^[A-Za-z0-9._%+-]{1,64}@(?:[A-Za-z0-9-]{1,63}\\.){1,125}[A-Za-z]{2,63}$", value))
        ) {
            errors.rejectValue(field, "invalid-character", "invalid email");
        }

    }

    public static void validateLength(Errors errors, String field, boolean required, int min, int max) {
        String value = (String) errors.getFieldValue(field);
        if (value == null) {
            if (required) {
                errors.rejectValue(field, "required", "this is required");
            }
            return;
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, "invalid-character", "cannot be empty or whitespace only");
        value = value.trim();
        if (min > value.length()) {
            errors.rejectValue(field, "length", "must be at least " + min + " characters long");
        }
        if (max < value.length()) {
            errors.rejectValue(field, "length", "must be at max " + max + " characters long");
        }
    }

    public static void validateDate(Errors errors, String field, boolean required) {
        String value = (String) errors.getFieldValue(field);
        if (value == null) {
            if (required) {
                errors.rejectValue(field, "required", "this is required");
            }
            return;
        }
        if (!DateUtils.isValidDate(value)) {
            errors.rejectValue(field, "format", "valid date format: dd-mm-yyyy");
        }

    }

    public static void validateCommaSeparatedDigits(Errors errors, String field, boolean required) {
        String value = (String) errors.getFieldValue(field);
        if (value == null) {
            if (required) {
                errors.rejectValue(field, "required", "this is required");
            }
            return;
        }
        if (!Pattern.matches("^([0-9]+,)*[0-9]+$", value)) {
            errors.rejectValue(field, "invalid-character", "must be a comma separated list of digits");
        }
    }

    public static void validateImage(Errors errors, String field, boolean required) {
        MultipartFile value = (MultipartFile) errors.getFieldValue(field);
        if (value == null) {
            if (required) {
                errors.rejectValue(field, "required", "this is required");
            }
            return;
        }

        List<String> validExtensions = Arrays.asList("png", "jpeg", "jpg");
        validateExtensionAndContentType(value, field, errors, validExtensions);
    }

    private static void validateExtensionAndContentType(MultipartFile file, String field, Errors errors, List<String> validExtensions) {
        if (!FileUtils.isValidExtension(file, validExtensions)) {
            errors.rejectValue(field, "extension", "invalid file extension");
            return;
        }

        if (!FileUtils.isValidContentTypeByExtension(file, validExtensions)) {
            errors.rejectValue(field, "content-type", "invalid content type");
        }
    }
}
