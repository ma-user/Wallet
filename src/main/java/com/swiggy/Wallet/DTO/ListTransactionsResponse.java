package com.swiggy.Wallet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListTransactionsResponse {
    private List<WalletTransactionsDTO> transactions;
    private String message;
    private int statusCode;
}
