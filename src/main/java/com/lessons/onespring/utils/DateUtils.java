package com.lessons.onespring.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.lessons.onespring.constants.Constant.DOB_FORMATTER;

public class DateUtils {
    public static boolean isValidDate(String date) {
        return DateUtils.isValidDate(date, DOB_FORMATTER);
    }

    public static boolean isValidDate(String date, String pattern) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        return DateUtils.isValidDate(date, dateFormatter);
    }

    public static boolean isValidDate(String date, DateTimeFormatter dateFormatter) {
        if (date == null || dateFormatter == null) return false;
        try {
            dateFormatter.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
