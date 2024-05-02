package com.swiggy.Wallet.service;

import com.swiggy.Wallet.Builder.Builder;
import com.swiggy.Wallet.DTO.*;
import com.swiggy.Wallet.DataAccessLayer.TransactionRepository;
import com.swiggy.Wallet.DataAccessLayer.WalletTransactionRepository;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.entity.Money;
import com.swiggy.Wallet.entity.Transaction;
import com.swiggy.Wallet.entity.Wallet;
import com.swiggy.Wallet.entity.WalletTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.swiggy.Wallet.Constants.Constants.DEPOSIT;
import static com.swiggy.Wallet.Constants.Constants.WITHDRAW;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserService userService;

    private final WalletService walletService;

    private final WalletTransactionRepository walletTransactionRepository;


    public List<WalletTransactionsDTO> fetchAllTransactionsForUserWithWallet(String username, Long id, LocalDate date) {
        try {
            Wallet targetWallet = userService.findTargetWallet(username, id);
            List<WalletTransaction> walletTransactions = null;
            List<Transaction> transactions = null;

            if (date != null) {
                LocalDateTime startOfDay = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()).atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
//                transactions = transactionRepository.findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(targetWallet, startOfDay, endOfDay, targetWallet, startOfDay, endOfDay);
                walletTransactions = walletTransactionRepository.findAllByWalletAndTimestampBetween(targetWallet, startOfDay, endOfDay);
            } else {
//                transactions = transactionRepository.findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(targetWallet, targetWallet);
                walletTransactions = walletTransactionRepository.findAllByWalletOrderByTimestampDesc(targetWallet);
            }

            if (walletTransactions == null) {
                throw new NullPointerException("Received Transactions list for user " + username + " at date "+ date + " as null");
            }

            return Builder.buildTransactionDTO(walletTransactions);
        } catch (UsernameNotFoundException | NullPointerException | NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error querying database", e);
        }
    }

    private Transaction createAndSaveTransaction(Wallet senderWallet, Wallet receiverWallet, Money amount, Money serviceFee) {
        Transaction senderTransaction = new Transaction(senderWallet, receiverWallet, amount, LocalDateTime.now(), serviceFee);
        try {
            return transactionRepository.save(senderTransaction);
        } catch (Exception e) {
            throw new DataAccessException("Error saving data to the repository", e);
        }
    }

    public TransferResponse transfer(Long senderWalletId, String senderUsername, TransferRequest transferRequest) {
        Long receiverWalletId = transferRequest.getReceiverWalletId();
        String receiverUsername = transferRequest.getReceiverUsername();
        Money transferMoney = transferRequest.getTransferMoney();
        transferMoney.validate();

        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(transferMoney);

        try {
            Wallet senderWallet = walletService.processRequestOnWalletAndSave(senderWalletId, senderUsername, walletTransactionRequest, DEPOSIT);
            Wallet receiverWallet = walletService.processRequestOnWalletAndSave(receiverWalletId, receiverUsername, walletTransactionRequest, WITHDRAW);

            Money serviceFee = senderWallet.calculateServiceFee(transferMoney);
            senderWallet.withdraw(serviceFee);

            Transaction transaction = createAndSaveTransaction(senderWallet, receiverWallet, transferMoney, serviceFee);

            return Builder.buildTransferResponse(transaction);
        } catch (InsufficientFundsException | CurrencyMismatchException | IllegalArgumentException | UsernameNotFoundException | NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error saving data to the repository", e);
        }
    }
}