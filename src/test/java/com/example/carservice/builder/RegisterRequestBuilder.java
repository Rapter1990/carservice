package com.example.carservice.builder;

import com.example.carservice.auth.model.dto.request.RegisterRequest;
import com.example.carservice.auth.model.enums.UserType;

import java.time.LocalDate;

public class RegisterRequestBuilder extends BaseBuilder<RegisterRequest> {

    public RegisterRequestBuilder() {
        super(RegisterRequest.class);
    }

    /**
     * Pre-populates the builder with valid Manager fields.
     */
    public RegisterRequestBuilder withAdminValidFields() {
        return this
                .withEmail("admin@example.com")
                .withPassword("adminpassword")
                .withFirstName("adminFirstName")
                .withLastName("adminLastName")
                .withPhoneNumber("10987654321")
                .withUserType(UserType.ADMIN);
    }

    /**
     * Pre-populates the builder with valid Employee fields.
     */
    public RegisterRequestBuilder withUserValidFields() {
        return this
                .withEmail("user@example.com")
                .withPassword("userpassword")
                .withFirstName("userFirstName")
                .withLastName("userLastName")
                .withPhoneNumber("12345678910")
                .withUserType(UserType.USER);
    }

    public RegisterRequestBuilder withEmail(String email) {
        data.setEmail(email);
        return this;
    }

    public RegisterRequestBuilder withPassword(String password) {
        data.setPassword(password);
        return this;
    }

    public RegisterRequestBuilder withFirstName(String firstName) {
        data.setFirstName(firstName);
        return this;
    }

    public RegisterRequestBuilder withLastName(String lastName) {
        data.setLastName(lastName);
        return this;
    }

    public RegisterRequestBuilder withPhoneNumber(String phoneNumber) {
        data.setPhoneNumber(phoneNumber);
        return this;
    }

    public RegisterRequestBuilder withUserType(UserType userType) {
        data.setUserType(userType);
        return this;
    }

}
