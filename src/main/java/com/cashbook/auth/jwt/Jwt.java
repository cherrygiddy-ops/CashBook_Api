package com.cashbook.auth.jwt;

import com.cashbook.auth.users.Role;
import io.jsonwebtoken.Claims;
import lombok.Data;

import java.util.Date;


@Data
public class Jwt {
    private final String token;     // the compact JWT string
    private final Claims claims;    // decoded claims

    public Jwt(String token, Claims claims) {
        this.token = token;
        this.claims = claims;
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public Integer getUserId() {
        return Integer.parseInt(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return token; // return the original signed token string
    }

}
