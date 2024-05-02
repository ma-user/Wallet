package com.swiggy.Wallet.Builder;

import com.swiggy.Wallet.DTO.TransactionDTO;
import com.swiggy.Wallet.DTO.TransferResponse;
import com.swiggy.Wallet.DTO.WalletTransactionsDTO;
import com.swiggy.Wallet.Enum.WalletTransactionType;
import com.swiggy.Wallet.entity.Money;
import com.swiggy.Wallet.entity.Transaction;
import com.swiggy.Wallet.entity.Wallet;
import com.swiggy.Wallet.entity.WalletTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Builder {

    private static final String MESSAGE = "Transfer successful for user";

    public static TransferResponse buildTransferResponse(Transaction transaction) {
        return TransferResponse.builder()
                .senderWalletId(transaction.getSenderWallet().getId())
                .receiverWalletId(transaction.getReceiverWallet().getId())
                .transferAmount(transaction.getAmount())
                .serviceFee(transaction.getServiceFee())
                .timestamp(transaction.getTimestamp())
                .message(MESSAGE)
                .statusCode(200).build();
    }

    public static List<WalletTransactionsDTO> buildTransactionDTO(List<WalletTransaction> walletTransactions) {
        List<WalletTransactionsDTO> walletTransactionsDTOList = new ArrayList<>();

        for (WalletTransaction walletTransaction : walletTransactions) {
            walletTransactionsDTOList.add(
                    WalletTransactionsDTO.builder()
                            .transferAmount(walletTransaction.getAmount())
                            .timestamp(walletTransaction.getTimestamp())
                            .transactionType(walletTransaction.getTransactionType())
                            .build()
            );
        }

        return walletTransactionsDTOList;
    }

    public static WalletTransaction buildWalletTransaction(Wallet wallet, Money money, WalletTransactionType transactionType) {
        return WalletTransaction.builder()
                .wallet(wallet)
                .amount(money)
                .timestamp(LocalDateTime.now())
                .transactionType(transactionType).build();
    }
}
