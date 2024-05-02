package com.swiggy.Wallet.Controller;

import com.swiggy.Wallet.DTO.*;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.Exceptions.InvalidAuthenticationException;
import com.swiggy.Wallet.Utils.SecurityUtils;
import com.swiggy.Wallet.entity.Wallet;
import com.swiggy.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletCreateResponse> create() {
        try {
            String username = SecurityUtils.getCurrentUsername();
            Wallet wallet = walletService.create(username);
            return ResponseEntity.ok(new WalletCreateResponse(wallet, "Wallet created successfully for user: " + username, 201));
        } catch (InvalidAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new WalletCreateResponse(null, e.getMessage(), 401));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WalletCreateResponse(null, e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new WalletCreateResponse(null, e.getMessage(), 500));
        }
    }

    @GetMapping
    public ResponseEntity<WalletListResponse> fetchAllForUser() {
        try {
            String username = SecurityUtils.getCurrentUsername();
            Set<Wallet> wallets = walletService.fetchAllFor(username);
            return ResponseEntity.ok(new WalletListResponse(wallets, "Retrieved all wallets successfully for user: " + username, 200));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WalletListResponse(null, e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new WalletListResponse(null, e.getMessage(), 500));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletTransactionResponse> transfer(@PathVariable Long id, @RequestBody WalletTransactionRequest depositRequest, @RequestHeader("Transaction-Type") String transactionType) {
        try {
            String username = SecurityUtils.getCurrentUsername();
            Wallet wallet = walletService.performWalletTransaction(id, username, depositRequest, transactionType);
            return ResponseEntity.ok(new WalletTransactionResponse(wallet, "Successfully processed request on wallet for " + username, 200));
        } catch (InvalidAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new WalletTransactionResponse(null, e.getMessage(), 401));
        } catch (CurrencyMismatchException | IllegalArgumentException | InsufficientFundsException | UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new WalletTransactionResponse(null, e.getMessage(), 400));
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WalletTransactionResponse(null, e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new WalletTransactionResponse(null, e.getMessage(), 500));
        }
    }
}
