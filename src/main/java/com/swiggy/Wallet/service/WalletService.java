package com.swiggy.Wallet.service;

import com.swiggy.Wallet.Builder.Builder;
import com.swiggy.Wallet.DTO.*;
import com.swiggy.Wallet.DataAccessLayer.UserRepository;
import com.swiggy.Wallet.DataAccessLayer.WalletRepository;
import com.swiggy.Wallet.DataAccessLayer.WalletTransactionRepository;
import com.swiggy.Wallet.Enum.WalletTransactionType;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.swiggy.Wallet.Constants.Constants.DEPOSIT;
import static com.swiggy.Wallet.Constants.Constants.WITHDRAW;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final WalletTransactionRepository walletTransactionRepository;

    public Wallet create(String username) {
        try {
            User user = userService.findByUsername(username);
            Wallet newWallet = user.addWalletBasedOn(user.getLocation());
            userRepository.save(user);
            return newWallet;
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error saving data to the repository", e);
        }
    }

    public Set<Wallet> fetchAllFor(String username) {
        try {
            return userService.fetchWallets(username);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error while retrieving wallets from the repository for user: " + username, e);
        }
    }

    public Wallet performWalletTransaction(Long walletId, String username, WalletTransactionRequest depositRequest, String transactionType) {
        Money depositRequestMoney = depositRequest.getMoney();
        depositRequestMoney.validate();

        try {
            return processRequestOnWalletAndSave(walletId, username, depositRequest, transactionType);
        } catch (CurrencyMismatchException | IllegalArgumentException | NoSuchElementException | UsernameNotFoundException | InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Error saving data to the repository", e);
        }
    }

    public Wallet processRequestOnWalletAndSave(Long walletId, String username, WalletTransactionRequest walletTransactionRequest, String transactionType) {
        Wallet wallet = userService.findTargetWallet(username, walletId);
        Money convertedMoney = wallet.convertAmountToReceiverWalletCurrency(walletTransactionRequest.getMoney());
        WalletTransaction transaction;

        if (transactionType.equals(DEPOSIT)) {
            wallet.deposit(convertedMoney);
            transaction = Builder.buildWalletTransaction(wallet, convertedMoney, WalletTransactionType.DEPOSIT);
        } else if (transactionType.equals(WITHDRAW)){
            wallet.withdraw(convertedMoney);
            transaction = Builder.buildWalletTransaction(wallet, convertedMoney, WalletTransactionType.WITHDRAWAL);
        } else {
            throw new UnsupportedOperationException("Unsupported operation on wallet");
        }

        walletTransactionRepository.save(transaction);
        return walletRepository.save(wallet);
    }
}
