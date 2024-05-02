package com.swiggy.Wallet.service;

import com.swiggy.Wallet.DTO.UserRegistrationRequest;
import com.swiggy.Wallet.DataAccessLayer.UserRepository;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.entity.User;
import com.swiggy.Wallet.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRegistrationRequest userRegistrationRequest) {
        validatePasswordPattern(userRegistrationRequest.getPassword());

        if (userRepository.findByUsername(userRegistrationRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        try {
            User user = new User(
                    userRegistrationRequest.getUsername(),
                    passwordEncoder.encode(userRegistrationRequest.getPassword()),
                    userRegistrationRequest.getLocation()
            );

            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error while registering user to repository", e);
        }
    }

    private void validatePasswordPattern(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters");
        }
    }

    public void delete(Long id, String username) {
        try {
            User user = findByUsername(username);
            if(!Objects.equals(user.getId(), id)) {
                throw new AccessDeniedException("User " + username + " does not exist");
            }
            userRepository.delete(user);
        } catch (UsernameNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error while deleting user from repository", e);
        }
    }

    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found in the database");
        }
        return optionalUser.get();
    }

    public Wallet findTargetWallet(String username, Long walletId) {
        User user = findByUsername(username);

        Optional<Wallet> targetWallet = user.getWallets().stream()
                .filter(wallet -> wallet.getId().equals(walletId))
                .findFirst();

        if (targetWallet.isPresent()) {
            return targetWallet.get();
        } else {
            throw new NoSuchElementException("User " + username + " does not contain any wallet with id: " + walletId);
        }
    }

    public Set<Wallet> fetchWallets(String username) {
        User user = findByUsername(username);
        return user.getWallets();
    }
}
