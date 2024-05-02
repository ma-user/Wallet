package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Wallet;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponse {
    private Wallet wallet;
    private String message;
    private int statusCode;
}
