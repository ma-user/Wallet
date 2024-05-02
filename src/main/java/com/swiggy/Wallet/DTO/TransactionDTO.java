package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long senderWalletId;
    private Long receiverWalletId;
    private Money transferAmount;
    private Money serviceFee;
    private LocalDateTime timestamp;
}
