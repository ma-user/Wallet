package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.Enum.WalletTransactionType;
import com.swiggy.Wallet.entity.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionsDTO {
    private Money transferAmount;
    private LocalDateTime timestamp;
    private WalletTransactionType transactionType;
}
