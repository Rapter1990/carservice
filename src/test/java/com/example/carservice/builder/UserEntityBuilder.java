package com.example.carservice.builder;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.model.enums.UserType;

import java.util.UUID;

public class UserEntityBuilder extends BaseBuilder<UserEntity> {

    public UserEntityBuilder() {
        super(UserEntity.class);
    }

    public UserEntityBuilder withValidFields() {
        return this
                .withId(UUID.randomUUID().toString())
                .withEmail("userexample@example.com")
                .withPassword("userpassword")
                .withFirstName("User FirstName")
                .withLastName("User LastName")
                .withPhoneNumber("1234567890")
                .withUserType(UserType.USER)
                .withUserStatus(UserStatus.ACTIVE);
    }

    public UserEntityBuilder withId(String id) {
        data.setId(id);
        return this;
    }

    public UserEntityBuilder withEmail(String email) {
        data.setEmail(email);
        return this;
    }

    public UserEntityBuilder withPassword(String password) {
        data.setPassword(password);
        return this;
    }

    public UserEntityBuilder withFirstName(String firstName) {
        data.setFirstName(firstName);
        return this;
    }

    public UserEntityBuilder withLastName(String lastName) {
        data.setLastName(lastName);
        return this;
    }

    public UserEntityBuilder withPhoneNumber(String phoneNumber) {
        data.setPhoneNumber(phoneNumber);
        return this;
    }

    public UserEntityBuilder withUserType(UserType userType) {
        data.setUserType(userType);
        return this;
    }

    public UserEntityBuilder withUserStatus(UserStatus userStatus) {
        data.setUserStatus(userStatus);
        return this;
    }

}
