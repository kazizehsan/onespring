package com.lessons.onespring.controllers;

import com.lessons.onespring.aop.AspectEventCatcher;
import com.lessons.onespring.aop.AspectEventType;
import com.lessons.onespring.controllers.requests.UserCreateRequest;
import com.lessons.onespring.controllers.requests.UserUpdateByAdminRequest;
import com.lessons.onespring.controllers.requests.UserUpdateSelfRequest;
import com.lessons.onespring.controllers.validators.UserCreateRequestValidator;
import com.lessons.onespring.dto.UserDto;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.exceptions.BadRequestException;
import com.lessons.onespring.services.impl.FileStorageService;
import com.lessons.onespring.services.intf.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.lessons.onespring.constants.Constant.PRIVILEGE_ADMINISTRATOR;
import static com.lessons.onespring.utils.ValidatorUtils.throwValidationException;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private FileStorageService fileStorageService;

    private UserCreateRequestValidator userCreateRequestValidator;

    public UserController(
            UserService userService,
            FileStorageService fileStorageService,
            UserCreateRequestValidator userCreateRequestValidator
    ) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.userCreateRequestValidator = userCreateRequestValidator;
    }

    @InitBinder("userCreateRequest")
    public void addValidatorForUserCreateRequest(WebDataBinder dataBinder) {
        dataBinder.setValidator(userCreateRequestValidator);
    }

    @ModelAttribute
    public void preRequestModelPopulation(
    ) {
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public List<UserDto> findAll() {
        return userService.findAll()
                .stream()
                .map(UserDto::entityToDto)
                .collect(Collectors.toList())
                ;
    }

    @AspectEventCatcher(eventType = AspectEventType.USER_CREATE)
    @PostMapping("/users")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public UserDto create(
            @Valid @ModelAttribute("userCreateRequest") UserCreateRequest userCreateRequest,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throwValidationException(result);
        }
        return UserDto.entityToDto(userService.create(userCreateRequest));
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return UserDto.entityToDto(user);
    }

    @AspectEventCatcher(eventType = AspectEventType.USER_UPDATE)
    @PutMapping("/me")
    public UserDto updateCurrentUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateSelfRequest userUpdateSelfRequest) {
        return UserDto.entityToDto(
                userService.updateSelf(user, userUpdateSelfRequest)
            );
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public UserDto findById(@PathVariable Long id) {
        return UserDto.entityToDto(userService.findById(id));
    }

    @AspectEventCatcher(eventType = AspectEventType.USER_UPDATE)
    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public UserDto updateById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateByAdminRequest userUpdateByAdminRequest) {
        if (user.getId().equals(id)) {
            throw new BadRequestException("A user can update their own personal details only.");
        }

        UserDto userDto = UserDto.entityToDto(
                userService.updateById(id, userUpdateByAdminRequest)
        );

        return userDto;
    }

    @AspectEventCatcher(eventType = AspectEventType.USER_DELETE)
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + PRIVILEGE_ADMINISTRATOR + "')")
    public ResponseEntity<?> deleteById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user.getId().equals(id)) {
            throw new BadRequestException("Users cannot delete themselves.");
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
