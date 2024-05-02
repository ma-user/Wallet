package com.swiggy.Wallet.Utils;

import com.swiggy.Wallet.Exceptions.InvalidAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new InvalidAuthenticationException("No authentication found");
        }

        if (!authentication.isAuthenticated()) {
            throw new InvalidAuthenticationException("Authentication not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new InvalidAuthenticationException("Principal is not an instance of UserDetails");
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}
