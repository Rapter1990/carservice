package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.response.UserResponse;
import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.model.enums.UserType;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserToUserResponseMapperTest {

    private final UserToUserResponseMapperImpl mapper = new UserToUserResponseMapperImpl();

    @Test
    void testMap_whenSourceIsNull_thenReturnNull() {

        // Given
        Collection<User> sources = null;

        // When
        List<UserResponse> result = mapper.map(sources);

        // Then
        assertNull(result);

    }

    @Test
    void testMap_whenSourceIsEmpty_thenReturnEmptyList() {

        // Given
        Collection<User> sources = Collections.emptyList();

        // When
        List<UserResponse> result = mapper.map(sources);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void testMap_whenSingleUser_thenReturnSingleUserResponse() {

        // Given
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .userType(UserType.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        Collection<User> users = List.of(user);

        // When
        List<UserResponse> result = mapper.map(users);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        UserResponse response = result.get(0);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(user.getUserType(), response.getUserType());
        assertEquals(user.getUserStatus(), response.getUserStatus());

    }

    @Test
    void testMap_whenMultipleUsers_thenReturnMappedList() {

        // Given
        User user1 = User.builder()
                .id(UUID.randomUUID().toString())
                .email("user1@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .phoneNumber("111")
                .userType(UserType.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID().toString())
                .email("user2@example.com")
                .firstName("Bob")
                .lastName("Brown")
                .phoneNumber("222")
                .userType(UserType.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        Collection<User> users = List.of(user1, user2);

        // When
        List<UserResponse> result = mapper.map(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());

    }

}