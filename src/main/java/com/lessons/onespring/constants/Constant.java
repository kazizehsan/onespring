package com.lessons.onespring.constants;

import java.time.format.DateTimeFormatter;

public class Constant {
    //DATE FORMAT
    public static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");


    //URLS
    public static final String PASSWORD_SET_URL = "/password/set";
    public static final String REFRESH_TOKEN_URL = "/refresh-token";


    //USER ACCOUNT STATUS
    public static final int PASSWORD_CHANGE_REQUIRED = -1;
    public static final int NORMAL_ACCOUNT_STATUS = 0;


    //PRIVILEGES
    public static final String PRIVILEGE_ADMINISTRATOR = "ADMIN";
}
