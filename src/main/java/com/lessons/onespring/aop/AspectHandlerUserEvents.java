package com.lessons.onespring.aop;

import com.lessons.onespring.controllers.requests.LoginRequest;
import com.lessons.onespring.dto.PrivilegeDto;
import com.lessons.onespring.dto.UserDto;
import com.lessons.onespring.entities.Privilege;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.repositories.UserRepository;
import com.lessons.onespring.services.intf.AuditLogService;
import com.lessons.onespring.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AspectHandlerUserEvents {

    private AuditLogService auditLogService;
    private UserRepository userRepository;

    public AspectHandlerUserEvents(AuditLogService auditLogService, UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public Object userUpdateHandler(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request) throws Throwable {
        AspectEventDetails aspectEventDetails = new AspectEventDetails();
        aspectEventDetails.setPath(request.getRequestURI());
        Long idOfUserToBeUpdated = null;
        for (Object arg: proceedingJoinPoint.getArgs()) {
            if (arg instanceof Long) {
                idOfUserToBeUpdated = (Long) arg;
            }
        }
        User userToBeUpdated = null;
        if (idOfUserToBeUpdated != null) {
            Optional<User> optionalUserToBeUpdatedByAdmin = userRepository.findById(idOfUserToBeUpdated);
            if (optionalUserToBeUpdatedByAdmin.isPresent()) {
                userToBeUpdated = optionalUserToBeUpdatedByAdmin.get();
            }
        } else {
            userToBeUpdated = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (userToBeUpdated != null) {
            aspectEventDetails.setResource_id(userToBeUpdated.getId().toString());
            aspectEventDetails.setResource_name(User.class.getSimpleName());
            aspectEventDetails.addPreviousValue("name", userToBeUpdated.getName());
            aspectEventDetails.addPreviousValue("dob", userToBeUpdated.getDob());
            aspectEventDetails.addPreviousValue("enabled", userToBeUpdated.isEnabled());
            aspectEventDetails.addPreviousValue("privileges", userToBeUpdated.getPrivileges()
                    .stream().map(Privilege::getId).collect(Collectors.toList()));
        }

        Object result = proceedingJoinPoint.proceed();

        if (idOfUserToBeUpdated != null) {
            userToBeUpdated = userRepository.findById(idOfUserToBeUpdated).get();
        } else {
            userToBeUpdated = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        aspectEventDetails.addNewValue("name", userToBeUpdated.getName());
        aspectEventDetails.addNewValue("dob", userToBeUpdated.getDob());
        aspectEventDetails.addNewValue("enabled", userToBeUpdated.isEnabled());
        aspectEventDetails.addNewValue("privileges", userToBeUpdated.getPrivileges()
                .stream().map(Privilege::getId).collect(Collectors.toList()));

        auditLogService.save(true, HttpUtils.getClientIp(request), AspectEventType.USER_UPDATE.toString(), aspectEventDetails.getJson());

        return result;
    }

    public Object userDeleteHandler(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request) throws Throwable {
        AspectEventDetails aspectEventDetails = new AspectEventDetails();
        aspectEventDetails.setPath(request.getRequestURI());
        Long idOfUserToBeDeleted = null;
        for (Object arg: proceedingJoinPoint.getArgs()) {
            if (arg instanceof Long) {
                idOfUserToBeDeleted = (Long) arg;
            }
        }
        User userToBeDeleted;
        Optional<User> optionalUserToBeDeleted = userRepository.findById(idOfUserToBeDeleted);
        if (optionalUserToBeDeleted.isPresent()) {
            userToBeDeleted = optionalUserToBeDeleted.get();

            aspectEventDetails.setResource_id(userToBeDeleted.getId().toString());
            aspectEventDetails.setResource_name(User.class.getSimpleName());
            aspectEventDetails.addPreviousValue("email", userToBeDeleted.getEmail());
            aspectEventDetails.addPreviousValue("dob", userToBeDeleted.getDob());
            aspectEventDetails.addPreviousValue("name", userToBeDeleted.getName());
            aspectEventDetails.addPreviousValue("privileges", userToBeDeleted.getPrivileges()
                    .stream().map(Privilege::getId).collect(Collectors.toList()));
        }

        Object result = proceedingJoinPoint.proceed();

        auditLogService.save(true, HttpUtils.getClientIp(request), AspectEventType.USER_DELETE.toString(), aspectEventDetails.getJson());

        return result;
    }

    public void userLoginHandler(JoinPoint joinPoint, HttpServletRequest request, Object result) throws JsonProcessingException {
        AspectEventDetails aspectEventDetails = new AspectEventDetails();
        aspectEventDetails.setPath(request.getRequestURI());
        LoginRequest loginRequest = null;
        for (Object arg: joinPoint.getArgs()) {
            if (arg instanceof LoginRequest) {
                loginRequest = (LoginRequest) arg;
            }
        }

        User createdBy = userRepository.findByEmail(loginRequest.getEmail()).get();
        aspectEventDetails.setResource_id(createdBy.getId().toString());
        aspectEventDetails.setResource_name(User.class.getSimpleName());

        auditLogService.save(createdBy, true, HttpUtils.getClientIp(request), AspectEventType.LOGIN.toString(), aspectEventDetails.getJson());
    }

    public void userCreateHandler(HttpServletRequest request, Object result) throws JsonProcessingException {
        AspectEventDetails aspectEventDetails = new AspectEventDetails();
        aspectEventDetails.setPath(request.getRequestURI());
        if (result instanceof UserDto) {
            UserDto createdUserDto = (UserDto) result;

            aspectEventDetails.setResource_id(createdUserDto.getId().toString());
            aspectEventDetails.setResource_name(User.class.getSimpleName());
            aspectEventDetails.addNewValue("email", createdUserDto.getEmail());
            aspectEventDetails.addNewValue("name", createdUserDto.getName());
            aspectEventDetails.addNewValue("dob", createdUserDto.getDob());
            aspectEventDetails.addNewValue("privileges", createdUserDto.getPrivileges()
                    .stream().map(PrivilegeDto::getId).collect(Collectors.toList()));
        }

        auditLogService.save(true, HttpUtils.getClientIp(request), AspectEventType.USER_CREATE.toString(), aspectEventDetails.getJson());
    }
}
