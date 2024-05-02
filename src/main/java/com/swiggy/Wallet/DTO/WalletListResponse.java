package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletListResponse {
    private Set<Wallet> wallets;
    private String message;
    private int statusCode;
}
