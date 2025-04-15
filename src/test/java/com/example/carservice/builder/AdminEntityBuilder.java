package com.example.carservice.builder;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.model.enums.UserType;

import java.util.UUID;

public class AdminEntityBuilder extends BaseBuilder<UserEntity> {


    public AdminEntityBuilder() {
        super(UserEntity.class);
    }

    public AdminEntityBuilder withValidFields() {
        return this
                .withId(UUID.randomUUID().toString())
                .withEmail("adminexample@example.com")
                .withPassword("adminpassword")
                .withFirstName("Admin First Name")
                .withLastName("Admin Last Name")
                .withPhoneNumber("1234567890")
                .withUserType(UserType.ADMIN)
                .withUserStatus(UserStatus.ACTIVE);
    }

    public AdminEntityBuilder withId(String id) {
        data.setId(id);
        return this;
    }

    public AdminEntityBuilder withEmail(String email) {
        data.setEmail(email);
        return this;
    }

    public AdminEntityBuilder withPassword(String password) {
        data.setPassword(password);
        return this;
    }

    public AdminEntityBuilder withFirstName(String firstName) {
        data.setFirstName(firstName);
        return this;
    }

    public AdminEntityBuilder withLastName(String lastName) {
        data.setLastName(lastName);
        return this;
    }

    public AdminEntityBuilder withPhoneNumber(String phoneNumber) {
        data.setPhoneNumber(phoneNumber);
        return this;
    }

    public AdminEntityBuilder withUserType(UserType userType) {
        data.setUserType(userType);
        return this;
    }

    public AdminEntityBuilder withUserStatus(UserStatus userStatus) {
        data.setUserStatus(userStatus);
        return this;
    }

}
