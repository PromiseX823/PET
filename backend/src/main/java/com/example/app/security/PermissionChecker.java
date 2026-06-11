package com.example.app.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component("permissionChecker")
@RequiredArgsConstructor
public class PermissionChecker {

    public boolean checkPermission(Object root) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No authentication found");
                return false;
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UserDetails)) {
                log.debug("Principal is not UserDetails");
                return false;
            }

            String username = ((UserDetails) principal).getUsername();
            String role = getRoleFromAuthentication(authentication);
            
            log.debug("Checking permission for user: {}, role: {}", username, role);

            Method method = root.getClass().getMethod("getTarget");
            Object target = method.invoke(root);
            
            RequirePermission annotation = target.getClass().getAnnotation(RequirePermission.class);
            if (annotation == null) {
                return true;
            }

            String[] requiredRoles = annotation.roles();
            String[] requiredPermissions = annotation.permissions();

            if (requiredRoles.length > 0) {
                List<String> roleList = Arrays.asList(requiredRoles);
                if (!roleList.contains(role) && !roleList.contains("ALL")) {
                    log.warn("User {} with role {} does not have required roles: {}", username, role, Arrays.toString(requiredRoles));
                    return false;
                }
            }

            if (requiredPermissions.length > 0) {
                List<String> permissionList = Arrays.asList(requiredPermissions);
                if (!hasAnyPermission(role, permissionList)) {
                    log.warn("User {} with role {} does not have required permissions: {}", username, role, Arrays.toString(requiredPermissions));
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error checking permission", e);
            return false;
        }
    }

    private String getRoleFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst()
                .map(role -> role.replace("ROLE_", ""))
                .orElse("USER");
    }

    private boolean hasAnyPermission(String role, List<String> requiredPermissions) {
        if ("ADMIN".equals(role)) {
            return true;
        }

        List<String> userPermissions = getPermissionsForRole(role);
        return requiredPermissions.stream().anyMatch(userPermissions::contains);
    }

    private List<String> getPermissionsForRole(String role) {
        return switch (role) {
            case "ADMIN" -> List.of("user:manage", "pet:manage", "photo:manage", "adoption:manage", "comment:manage", "system:manage");
            case "MANAGER" -> List.of("pet:manage", "photo:manage", "adoption:manage");
            case "USER" -> List.of("photo:upload", "comment:create", "adoption:apply");
            default -> List.of();
        };
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.toUpperCase()));
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isManager() {
        return hasRole("MANAGER") || hasRole("ADMIN");
    }

    public boolean isUser() {
        return hasRole("USER") || hasRole("MANAGER") || hasRole("ADMIN");
    }
}