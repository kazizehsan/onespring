package com.lessons.onespring.security;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${security.jwt.token.secret-key:secret}")
    private String jwtSecretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds; // 60 minutes

    @Value("${security.jwt.refresh-token.expire-length:43200000}")
    private long refreshValidityInMilliseconds; // 12 hours

    public String generateToken(String subject, String type) {
        Claims claims = Jwts.claims().setSubject(subject).setId(UUID.randomUUID().toString());

        Date issuedAt = new Date();
        Date expiryDate;

        if (type.equals("refresh")) {
            expiryDate = new Date(issuedAt.getTime() + refreshValidityInMilliseconds);
        } else {
            expiryDate = new Date(issuedAt.getTime() + validityInMilliseconds);
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("type", type);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .addClaims(extraClaims)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        Map<String, String[]> queryParameters = getQueryParameters(request);
        if (queryParameters.containsKey("token") && queryParameters.get("token").length == 1) {
            String token = queryParameters.get("token")[0];
            if (StringUtils.hasText(token)) {
                return token;
            }
        }
        return null;
    }

    public String getSubjectFromJwt(String token) {
        // Validation occurs here automatically when `parseClaimsJws` is invoked.
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getJtiFromJwt(String token) {
        // Validation occurs here automatically when `parseClaimsJws` is invoked.
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getId();
    }

    private static Map<String, String[]> getQueryParameters(HttpServletRequest request) {
        Map<String, String[]> queryParameters = new HashMap<>();
        String queryString = request.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            String[] values = queryParameters.get(keyValuePair[0]);
            values = ArrayUtils.add(values, keyValuePair.length == 1 ? "" : keyValuePair[1]); //length is one if no value is available.
            queryParameters.put(keyValuePair[0], values);
        }
        return queryParameters;
    }
}
