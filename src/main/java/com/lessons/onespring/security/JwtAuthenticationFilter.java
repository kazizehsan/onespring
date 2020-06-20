package com.lessons.onespring.security;

import com.lessons.onespring.controllers.responses.ApiErrorResponse;
import com.lessons.onespring.entities.User;
import com.lessons.onespring.exceptions.PasswordChangeRequiredException;
import com.lessons.onespring.exceptions.UserNotEnabledException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.lessons.onespring.constants.Constant.PASSWORD_SET_URL;
import static com.lessons.onespring.constants.Constant.REFRESH_TOKEN_URL;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtTokenProvider.getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                String keyInRedis =  jwtTokenProvider.getJtiFromJwt(jwt);

                if (redisTemplate.hasKey(keyInRedis)) {
                    String userName = jwtTokenProvider.getSubjectFromJwt(jwt);
                    String storedRedisValue = redisTemplate.opsForValue().get(keyInRedis);

                    boolean redisValueMatches;
                    if (request.getRequestURI().equals(REFRESH_TOKEN_URL)) {
                        redisValueMatches = ("refresh:" + userName).equals(storedRedisValue);
                    } else {
                        redisValueMatches = userName.equals(storedRedisValue);
                    }

                    if (redisValueMatches) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                        if (!userDetails.isEnabled()) {
                            redisTemplate.delete(keyInRedis);
                            throw new UserNotEnabledException();
                        }

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request, userDetails);

                        if (!request.getRequestURI().equals(PASSWORD_SET_URL)) {
                            User user = getUser(usernamePasswordAuthenticationToken);
                            if (user.isPasswordChangeRequired()) {
                                throw new PasswordChangeRequiredException();
                            }
                        }

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }
        } catch (Exception ex) {
            int httpStatus = HttpStatus.UNAUTHORIZED.value();
            String errorMessage = "Failed to authorize user.";
            if (ex instanceof UserNotEnabledException) {
                httpStatus = HttpStatus.LOCKED.value();
                errorMessage = "User is not enabled";
            } else if (ex instanceof SignatureException) {
                errorMessage = "Invalid JWT signature";
            } else if (ex instanceof MalformedJwtException) {
                errorMessage = "Invalid JWT token";
            } else if (ex instanceof ExpiredJwtException) {
                errorMessage = "Expired JWT token";
            } else if (ex instanceof UnsupportedJwtException) {
                errorMessage = "Unsupported JWT token";
            } else if (ex instanceof IllegalArgumentException) {
                errorMessage = "JWT claims string is empty.";
            } else if (ex instanceof PasswordChangeRequiredException) {
                errorMessage = ex.getMessage();
            }

            ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                    new Date(),
                    httpStatus,
                    errorMessage,
                    request.getRequestURI()
            );

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(apiErrorResponse));
            response.setStatus(httpStatus);
            response.addHeader("Content-Type", "application/json");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
