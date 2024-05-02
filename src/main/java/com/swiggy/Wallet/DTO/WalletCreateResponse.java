package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WalletCreateResponse {
    private Wallet wallet;
    private String message;
    private int statusCode;
}
