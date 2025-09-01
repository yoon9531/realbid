package com.sy.userservice.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private UserDetails principal;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        setAuthenticated(false);
    }
    public String getCredentials() { return token; }
    public Object getPrincipal() { return principal; }
    public void setPrincipal(UserDetails principal) { this.principal = principal; }
}
