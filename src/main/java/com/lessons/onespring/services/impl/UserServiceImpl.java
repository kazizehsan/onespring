package com.lessons.onespring.services.impl;


import com.lessons.onespring.controllers.requests.*;
import com.lessons.onespring.entities.Privilege;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.exceptions.BadRequestException;
import com.lessons.onespring.exceptions.EntityNotFoundException;
import com.lessons.onespring.repositories.PrivilegeRepository;
import com.lessons.onespring.repositories.UserRepository;
import com.lessons.onespring.services.intf.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.lessons.onespring.constants.Constant.*;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    private PrivilegeRepository privilegeRepository;

    private FileStorageService fileStorageService;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            PrivilegeRepository privilegeRepository,
            FileStorageService fileStorageService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.fileStorageService = fileStorageService;
    }

    public boolean isEnabled(String email) {
        User user = findByEmail(email);

        return user.isEnabled();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private String uploadFile(MultipartFile file) {
        return fileStorageService.storeFile(file);
    }

    @Transactional
    public User create(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
            BadRequestException badRequestException = new BadRequestException("Validation failed");
            badRequestException.setFieldError(
                    "email",
                    "user with the same email already exists"
            );
            throw badRequestException;
        }

        Set<Long> privilegeIds = Arrays.stream(userCreateRequest.getPrivileges().split(","))
                .map(Long::parseLong).collect(Collectors.toSet());
        Set<Privilege> privileges = privilegeRepository.findAllByIdIn(privilegeIds);
        if (privileges.size() != privilegeIds.size()) {
            BadRequestException badRequestException = new BadRequestException("Validation failed");
            badRequestException.setFieldError(
                    "privileges",
                    "All submitted Privileges must exist in database"
            );
            throw badRequestException;
        }

        User user = new User();
        user.setEmail(userCreateRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setName(userCreateRequest.getName());
        user.setDob(LocalDate.parse(userCreateRequest.getDob(), DOB_FORMATTER));
        user.setEnabled(true);
        user.setPrivileges(new HashSet<>(privileges));
        user.setAccountStatus(NORMAL_ACCOUNT_STATUS);
        if (userCreateRequest.getPhoto() != null) {
            user.setPhoto(uploadFile(userCreateRequest.getPhoto()));
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", "id", id));
    }

    @Transactional
    public User updateSelf(User user, UserUpdateSelfRequest userUpdateSelfRequest) {
        if (userUpdateSelfRequest.getName() != null) {
            user.setName(userUpdateSelfRequest.getName());
        }
        if (userUpdateSelfRequest.getDob() != null) {
            user.setDob(LocalDate.parse(userUpdateSelfRequest.getDob(), DOB_FORMATTER));
        }
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateById(Long id, UserUpdateByAdminRequest userUpdateByAdminRequest) {
        User user = findById(id);

        user.setName(userUpdateByAdminRequest.getName());
        if (userUpdateByAdminRequest.getDob() != null) {
            user.setDob(LocalDate.parse(userUpdateByAdminRequest.getDob(), DOB_FORMATTER));
        }
        user.setEnabled(userUpdateByAdminRequest.getEnabled());
        if (userUpdateByAdminRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateByAdminRequest.getPassword()));
        }

        Set<Privilege> privileges = privilegeRepository.findAllByIdIn(userUpdateByAdminRequest.getPrivileges());
        if (privileges.size() != userUpdateByAdminRequest.getPrivileges().size()) {
            BadRequestException badRequestException = new BadRequestException("Validation failed");
            badRequestException.setFieldError(
                    "privileges",
                    "All submitted Privileges must exist in database"
            );
            throw badRequestException;
        }
        user.setPrivileges(new HashSet<>(privileges));

        userRepository.save(user);

        return user;
    }

    @Transactional
    public void deleteById(Long id) {
        User user = findById(id);
        for (Privilege privilege: user.getPrivileges()) {
            if (privilege.getName().equals(PRIVILEGE_ADMINISTRATOR)) {
                throw new BadRequestException("Users with Admin privileges cannot be deleted.");
            }
        }
        user.setDeletedAt(new Date());
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } else {
            BadRequestException badRequestException = new BadRequestException("Validation failed");
            badRequestException.setFieldError(
                    "old_password",
                    "Old password is incorrect"
            );
            throw badRequestException;
        }
    }

    @Override
    public void setPassword(User user, SetPasswordRequest request) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(NORMAL_ACCOUNT_STATUS);
        userRepository.save(user);
    }
}
