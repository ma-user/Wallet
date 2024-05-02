package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {
    private User user;
    private String message;
    private int statusCode;
}
