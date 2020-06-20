package com.lessons.onespring.controllers;


import com.lessons.onespring.aop.AspectEventCatcher;
import com.lessons.onespring.aop.AspectEventType;
import com.lessons.onespring.controllers.requests.ChangePasswordRequest;
import com.lessons.onespring.controllers.requests.LoginRequest;
import com.lessons.onespring.controllers.requests.SetPasswordRequest;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.exceptions.BadRequestException;
import com.lessons.onespring.security.JwtTokenProvider;
import com.lessons.onespring.services.intf.AuditLogService;
import com.lessons.onespring.services.intf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.lessons.onespring.constants.Constant.PASSWORD_SET_URL;
import static com.lessons.onespring.constants.Constant.REFRESH_TOKEN_URL;

@RestController
public class AuthController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;

    private UserService userService;

    private AuditLogService auditLogService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            AuditLogService auditLogService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds; // 60 minutes

    @Value("${security.jwt.refresh-token.expire-length:43200000}")
    private long refreshValidityInMilliseconds; // 12 hours

    @AspectEventCatcher(eventType = AspectEventType.LOGIN)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        Map<Object, Object> model = new HashMap<>();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String jwt = jwtTokenProvider.generateToken(loginRequest.getEmail(), "access");
        String refreshJwt = jwtTokenProvider.generateToken(loginRequest.getEmail(), "refresh");
        redisTemplate.opsForValue().set(jwtTokenProvider.getJtiFromJwt(jwt), loginRequest.getEmail());
        redisTemplate.expire(jwtTokenProvider.getJtiFromJwt(jwt), validityInMilliseconds, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(
                jwtTokenProvider.getJtiFromJwt(refreshJwt), "refresh:" + loginRequest.getEmail());
        redisTemplate.expire(
                jwtTokenProvider.getJtiFromJwt(refreshJwt), refreshValidityInMilliseconds, TimeUnit.MILLISECONDS);

        model.put("token", jwt);
        model.put("refresh-token", refreshJwt);

        User user = (User) authentication.getPrincipal();
        if (user.isPasswordChangeRequired()) {
            model.put("password_change_required", true);
        }

        return ResponseEntity.ok(model);
    }

    @AspectEventCatcher(eventType = AspectEventType.LOGOUT)
    @GetMapping("/logout")
    public ResponseEntity<?> logUserOut(HttpServletRequest request) {
        Map<String, String> model = new HashMap<>();
        String redisKey = jwtTokenProvider.getJtiFromJwt(jwtTokenProvider.getJwtFromRequest(request));
        redisTemplate.delete(redisKey);
        model.put("message", "successfully logged out user!");

        return ResponseEntity.ok(model);
    }

    @AspectEventCatcher(eventType = AspectEventType.CHANGE_PASSWORD)
    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(user, request);

        Map<String, String> model = new HashMap<>();
        model.put("message", "successfully changed password!");

        return ResponseEntity.ok(model);
    }

    @PostMapping(REFRESH_TOKEN_URL)
    public ResponseEntity<?> refreshToken(
            @AuthenticationPrincipal User user
    ) {
        String jwt = jwtTokenProvider.generateToken(user.getEmail(), "access");
        redisTemplate.opsForValue().set(jwtTokenProvider.getJtiFromJwt(jwt), user.getEmail());
        redisTemplate.expire(jwtTokenProvider.getJtiFromJwt(jwt), validityInMilliseconds, TimeUnit.MILLISECONDS);
        Map<String, String> model = new HashMap<>();
        model.put("token", jwt);
        return ResponseEntity.ok(model);
    }


    @AspectEventCatcher(eventType = AspectEventType.CHANGE_PASSWORD)
    @PostMapping(value = PASSWORD_SET_URL)
    public ResponseEntity<?> setPassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SetPasswordRequest setPasswordRequest) {

        if (!setPasswordRequest.getPassword().equals(setPasswordRequest.getConfirmPassword())) {
            BadRequestException badRequestException = new BadRequestException("validation failed");
            badRequestException.setFieldError("password", "password and confirm password does not match");
            throw badRequestException;
        }

        userService.setPassword(user, setPasswordRequest);

        Map<String, String> model = new HashMap<>();
        model.put("message", "successfully changed password!");

        return ResponseEntity.ok(model);
    }
}
