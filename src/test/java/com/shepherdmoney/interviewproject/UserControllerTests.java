package com.shepherdmoney.interviewproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shepherdmoney.interviewproject.controller.UserController;
import com.shepherdmoney.interviewproject.exception.BusinessException;
import com.shepherdmoney.interviewproject.response.ResponseEnum;
import com.shepherdmoney.interviewproject.service.UserService;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void deleteUserShouldReturnSuccessMessage() throws Exception {
        when(userService.deleteUser(anyInt())).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/user?userId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    public void deleteUserShouldReturnBadRequestIfUserDoesNotExist() throws Exception {
        // Given
        Mockito.doThrow(new BusinessException(ResponseEnum.PARAM_EXCEPTION))
                .when(userService)
                .deleteUser(anyInt());

        // When/Then
        mockMvc.perform(delete("/user")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturnUserId() throws Exception {
        // Arrange
        CreateUserPayload mockPayload = new CreateUserPayload();
        when(userService.createUser(any(CreateUserPayload.class))).thenReturn(1); // assuming the creation returns the id 1

        // Act & Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockPayload))) // helper method to convert object to JSON string
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1)); // assuming the id of the created user is returned
    }

    @Test
    public void createUserShouldReturnBadRequestIfCreationFails() throws Exception {
        // Arrange
        CreateUserPayload mockPayload = new CreateUserPayload();
        when(userService.createUser(any(CreateUserPayload.class)))
                .thenThrow(new BusinessException(ResponseEnum.PARAM_EXCEPTION));

        // Act & Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockPayload)))
                .andExpect(status().isBadRequest());
    }

    // Helper method to convert object to JSON string
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
