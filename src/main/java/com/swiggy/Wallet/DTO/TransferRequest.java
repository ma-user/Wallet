package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private Long receiverWalletId;
    private String receiverUsername;
    private Long receiverId;
    private Money transferMoney;
}
