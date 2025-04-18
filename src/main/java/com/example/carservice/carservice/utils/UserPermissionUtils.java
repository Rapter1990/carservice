package com.example.carservice.carservice.utils;

import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.enums.UserType;
import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;

/**
 * Utility class for validating user-level access permissions.
 * Used to ensure that a user (typically of type USER) cannot access or modify data belonging to another user.
 * Admin users bypass this check.
 */
@UtilityClass
public class UserPermissionUtils {

    /**
     * Validates whether the current user is authorized to access a resource associated with the target user ID.
     * Throws an {@link AccessDeniedException} if the user is not authorized.
     *
     * @param userIdentity  the identity of the currently authenticated user
     * @param targetUserId  the user ID of the resource being accessed
     */
    public void checkAccessPermission(UserIdentity userIdentity, String targetUserId) {
        final String currentUserId = userIdentity.getUserId();
        final UserType currentUserType = userIdentity.getUserType();

        if (currentUserType == UserType.ADMIN) {
            return; // Admins have full access
        }

        if (!targetUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized for another user.");
        }
    }

}

