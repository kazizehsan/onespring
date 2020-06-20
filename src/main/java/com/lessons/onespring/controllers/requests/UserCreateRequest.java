package com.lessons.onespring.controllers.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserCreateRequest {

    private String email;
    private String password;
    private String name;
    private String dob;
    private String privileges;
    private MultipartFile photo;

}
