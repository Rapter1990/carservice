package com.example.carservice.carservice.utils;

import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.enums.UserType;
import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;

@UtilityClass
public class UserPermissionUtils {

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

