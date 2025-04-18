package com.example.carservice.auth.model.dto.response;

import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.model.enums.UserType;
import lombok.*;

/**
 * Response DTO representing user profile information.
 * Typically returned after registration, login, or when fetching user details.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserType userType;
    private UserStatus userStatus;

}
