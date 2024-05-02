package com.swiggy.Wallet.DTO;

import com.swiggy.Wallet.entity.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WalletCreateRequest {
    private Money money;
}
