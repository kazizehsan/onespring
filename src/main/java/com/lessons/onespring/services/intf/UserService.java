package com.lessons.onespring.services.intf;

import com.lessons.onespring.controllers.requests.*;
import com.lessons.onespring.entities.User;

import java.util.List;

public interface UserService {
    boolean isEnabled(String email);

    List<User> findAll();

    User create(UserCreateRequest user);

    User findByEmail(String email);

    User findById(Long id);

    User updateSelf(User user, UserUpdateSelfRequest userUpdateSelfRequest);

    User updateById(Long id, UserUpdateByAdminRequest user);

    void deleteById(Long id);

    void changePassword(User user, ChangePasswordRequest request);

    void setPassword(User user, SetPasswordRequest request);
}
