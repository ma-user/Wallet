package com.swiggy.Wallet.Controller;

import com.swiggy.Wallet.DTO.*;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.Exceptions.InvalidAuthenticationException;
import com.swiggy.Wallet.Utils.SecurityUtils;
import com.swiggy.Wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/wallets/{id}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> fetchAllTransactionsForUserWithWallet(
            @PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            String username = SecurityUtils.getCurrentUsername();
            List<WalletTransactionsDTO> transactions = transactionService.fetchAllTransactionsForUserWithWallet(username, id, date);
            return ResponseEntity.ok(new ListTransactionsResponse(transactions, "Retrieved all transactions successfully for user " + username + " with wallet " + id, 200));
        } catch (InvalidAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ListTransactionsResponse(null, e.getMessage(), 401));
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ListTransactionsResponse(null, e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ListTransactionsResponse(null, e.getMessage(), 500));
        }
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@PathVariable Long id, @RequestBody TransferRequest transferRequest) {
        try {
            String senderUsername = SecurityUtils.getCurrentUsername();
            TransferResponse transferResponse = transactionService.transfer(id, senderUsername, transferRequest);
            return ResponseEntity.ok(transferResponse);
        } catch (InvalidAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TransferResponse(null, null, null, null, null, e.getMessage(), 401));
        } catch (CurrencyMismatchException | IllegalArgumentException | InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransferResponse(null, null, null, null, null, e.getMessage(), 400));
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TransferResponse(null, null, null, null, null, e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new TransferResponse(null, null, null, null, null, e.getMessage(), 500));
        }
    }
}
