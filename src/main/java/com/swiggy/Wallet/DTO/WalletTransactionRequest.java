package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionRequest {
    private Money money;
}
