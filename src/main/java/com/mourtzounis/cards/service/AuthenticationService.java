package com.mourtzounis.cards.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    public Long getUserIdFromPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}
