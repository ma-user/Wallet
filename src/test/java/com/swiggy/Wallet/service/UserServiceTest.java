package com.swiggy.Wallet.service;

import com.swiggy.Wallet.DTO.UserRegistrationRequest;
import com.swiggy.Wallet.DataAccessLayer.UserRepository;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.swiggy.Wallet.entity.User;

import java.util.Optional;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final UserRegistrationRequest registrationRequest = new UserRegistrationRequest(USERNAME, PASSWORD, VALID_LOCATION);

    @Test
    public void testRegister_WithInvalidPassword_ThrowsIllegalArgumentException() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(USERNAME, INVALID_PASSWORD, VALID_LOCATION);

        assertThrows(IllegalArgumentException.class, () -> userService.register(registrationRequest));

        verify(userRepository, never()).findByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegister_WithExistingUsername_ThrowsIllegalArgumentException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.register(registrationRequest));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegister_WithInvalidLocation_ThrowsIllegalArgumentException() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(USERNAME, PASSWORD, INVALID_LOCATION);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        assertThrows(IllegalArgumentException.class, () -> userService.register(userRegistrationRequest));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegister_ValidUserRegistration_ReturnsRegisteredUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.register(registrationRequest);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_UnknownRepositoryError_ThrowsDataAccessException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        doThrow(RuntimeException.class).when(userRepository).save(user);

        assertThrows(DataAccessException.class, () -> userService.register(registrationRequest));
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void testDelete_DifferentUser_ThrowsAccessDeniedException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> userService.delete(INVALID_USER_ID, USERNAME));
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).delete(user);
    }

    @Test
    public void testDelete_NoUserFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.delete(VALID_USER_ID, USERNAME));
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).delete(user);
    }

    @Test
    public void testDelete_Success() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        userService.delete(VALID_USER_ID, USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDelete_UnknownRepositoryError_ThrowsDataAccessException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        doThrow(RuntimeException.class).when(userRepository).delete(user);

        assertThrows(DataAccessException.class, () -> userService.delete(VALID_USER_ID, USERNAME));
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDelete_differentUserId_ThrowsAccessDeniedException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> userService.delete(INVALID_USER_ID, USERNAME));
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).delete(user);
    }
}