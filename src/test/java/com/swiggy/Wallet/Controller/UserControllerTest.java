package com.swiggy.Wallet.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.Wallet.DTO.UserRegistrationRequest;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.entity.User;
import com.swiggy.Wallet.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final UserRegistrationRequest registrationRequest = new UserRegistrationRequest(USERNAME, PASSWORD, VALID_LOCATION);

    private final User user = new User(VALID_USER_ID, USERNAME, PASSWORD);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister_ValidRegistrationRequest_ReturnsCreatedResponse() throws Exception {
        when(userService.register(any(UserRegistrationRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.username").value("username"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.statusCode").value(201));

        verify(userService, times(1)).register(any(UserRegistrationRequest.class));
        verify(userService, never()).delete(anyLong(), anyString());
    }

    @Test
    void testRegister_userAlreadyExists_ReturnsBadRequest() throws Exception {
        when(userService.register(any(UserRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exist. Choose another"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user").doesNotExist())
                .andExpect(jsonPath("$.message").value("Username already exist. Choose another"))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(userService, times(1)).register(any(UserRegistrationRequest.class));
        verify(userService, never()).delete(anyLong(), anyString());
    }

    @Test
    void testRegister_unexpectedRepositoryError_ReturnsInternalServerErrorResponse() throws Exception {
        when(userService.register(any(UserRegistrationRequest.class)))
                .thenThrow(new RuntimeException("Unexpected database error. Can't register user now."));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user").doesNotExist())
                .andExpect(jsonPath("$.message").value("Unexpected database error. Can't register user now."))
                .andExpect(jsonPath("$.statusCode").value(500));

        verify(userService, times(1)).register(any(UserRegistrationRequest.class));
        verify(userService, never()).delete(anyLong(), anyString());
    }

    @Test
    @WithMockUser
    public void testDelete_whenAuthorizedUserFound_Success() throws Exception {
        doNothing().when(userService).delete(anyLong(), anyString());

        mockMvc.perform(delete("/users/{id}", VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(userService, times(1)).delete(anyLong(), anyString());
        verify(userService, times(0)).register(any());
    }

    @Test
    @WithMockUser
    public void testDelete_whenAuthorizedUser_unexpectedRepositoryError_ReturnsInternalServerError() throws Exception {
        doThrow(new DataAccessException("Unexpected repository error occurred")).when(userService).delete(anyLong(), anyString());

        mockMvc.perform(delete("/users/{id}", VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).delete(anyLong(), anyString());
        verify(userService, times(0)).register(any());
    }

    @Test
    @WithMockUser
    public void testDelete_userNotFound_ReturnsNotFound() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userService).delete(anyLong(), anyString());

        mockMvc.perform(delete("/users/{id}", VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).delete(anyLong(), anyString());
        verify(userService, times(0)).register(any());
    }


    @Test
    @WithAnonymousUser
    public void testDelete_whenUnauthorizedUser_ReturnsStatusAsUnauthorized() throws Exception {
        doNothing().when(userService).delete(VALID_USER_ID, USERNAME);

        mockMvc.perform(delete("/users/{id}", VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(anyLong(), anyString());
        verify(userService, times(0)).register(any());
    }
}