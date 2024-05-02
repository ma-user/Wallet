package com.swiggy.Wallet.service;

import com.swiggy.Wallet.DTO.TransactionDTO;
import com.swiggy.Wallet.DataAccessLayer.TransactionRepository;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.entity.Money;
import com.swiggy.Wallet.entity.Transaction;
import com.swiggy.Wallet.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    public static final Money serviceFee = new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA));

//    private final TransferRequest transferRequest = new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));

    private final Wallet senderWallet = spy(new Wallet(new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA))));

    private final Wallet recipientWallet = spy(new Wallet(new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.CANADA))));

//    @Test
//    public void testFetchAllTransactionsForUserAndWalletWithNullDate_success() {
//        List<Transaction> mockTransactions = List.of(new Transaction(wallet, anotherWallet, amount, LocalDateTime.now(), serviceFee),
//                new Transaction(anotherWallet, wallet, anotherAmount, LocalDateTime.now(), serviceFee));
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
//        when(transactionRepository.findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockTransactions);
//
//        List<TransactionDTO> result = transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, VALID_WALLET_ID, null);
//
//        assertEquals(2, result.size());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(transactionRepository, times(1)).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
//        verify(transactionRepository, never()).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_success() {
//        List<Transaction> mockTransactions = List.of(new Transaction(wallet, anotherWallet, amount, LocalDateTime.now(), serviceFee),
//                new Transaction(anotherWallet, wallet, anotherAmount, LocalDateTime.now(), serviceFee));
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
//        when(transactionRepository.findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockTransactions);
//
//        List<TransactionDTO> result = transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, VALID_WALLET_ID, LocalDate.now());
//
//        assertEquals(2, result.size());
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(transactionRepository, never()).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
//        verify(transactionRepository, times(1)).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//    }

    @Test
    public void testFetchAllTransactionsForUserAndWalletOnDate_unknownRepositoryError_throwsDataAccessException() {
        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
        when(transactionRepository.findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenThrow(RuntimeException.class);

        assertThrows(DataAccessException.class, () -> transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, VALID_WALLET_ID, LocalDate.now()));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionRepository, times(1)).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
        verify(transactionRepository, never()).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
    }

    @Test
    public void testFetchAllTransactionsForUserAndWalletOnDate_nullList_throwsNullPointerException() {
        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
        when(transactionRepository.findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, VALID_WALLET_ID, LocalDate.now()));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionRepository, times(1)).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
        verify(transactionRepository, never()).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
    }

    @Test
    public void testFetchAllTransactionsForUserAndWalletOnDate_noUserFound_throwsUsernameNotFoundException() {
        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, VALID_WALLET_ID, LocalDate.now()));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionRepository, never()).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
        verify(transactionRepository, never()).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testFetchAllTransactionsForUserAndWalletOnDate_ForDifferentWallet_throwsNoSuchElementException() {
        when(userService.findTargetWallet(SENDER_USERNAME, INVALID_WALLET_ID)).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> transactionService.fetchAllTransactionsForUserWithWallet(SENDER_USERNAME, INVALID_WALLET_ID, LocalDate.now()));

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionRepository, never()).findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(any(Wallet.class), any(Wallet.class));
        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, INVALID_WALLET_ID);
        verify(transactionRepository, never()).findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Wallet.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    //    @Test
//    void testTransferWithNullMoney_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, null)));
//
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userService, never()).findTargetWallet(anyString(), anyLong());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testTransferWithNullCurrency_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.TEN, null))));
//
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userService, never()).findTargetWallet(anyString(), anyLong());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testTransferWithNullAmount_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(null, Currency.getInstance(Locale.CANADA)))));
//
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userService, never()).findTargetWallet(anyString(), anyLong());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testTransferWithNegativeAmount_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.valueOf(-10), Currency.getInstance(Locale.CANADA)))));
//
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userService, never()).findTargetWallet(anyString(), anyLong());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransfer_noSenderFound_throwsUsernameNotFoundException() {
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(UsernameNotFoundException.class);
//
//        assertThrows(UsernameNotFoundException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest));
//
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(0)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(senderWallet, times(0)).withdraw(any(Money.class));
//        verify(recipientWallet, times(0)).deposit(any(Money.class));
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransfer_noReceiverFound_throwsUsernameNotFoundException() {
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(senderWallet);
//        when(userService.findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID)).thenThrow(UsernameNotFoundException.class);
//
//        assertThrows(UsernameNotFoundException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest));
//
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(1)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(senderWallet, never()).withdraw(any(Money.class));
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransfer_whenNoWalletFound_throwsNoSuchElementException() {
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(NoSuchElementException.class);
//
//        assertThrows(NoSuchElementException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest));
//
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(0)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransfer_unknownRepositoryError_throwsDataAccessException() {
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(RuntimeException.class);
//
//        assertThrows(DataAccessException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest));
//
//        verify(userService, times(0)).findByUsername(anyString());
//        verify(walletRepository, times(0)).save(mockWallet);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransferWithSameCurrency_success() {
//        TransferRequest transferRequest = new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.TEN, Currency.getInstance(Locale.CANADA)));
//        Wallet expectedWallet = new Wallet(new Money(BigDecimal.valueOf(90), Currency.getInstance(Locale.CANADA)));
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(senderWallet);
//        when(userService.findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID)).thenReturn(recipientWallet);
//        when(walletRepository.save(any(Wallet.class))).thenReturn(senderWallet);
//
//        Wallet actualWallet = walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest);
//
//        assertEquals(expectedWallet, actualWallet);
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(1)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(walletRepository, times(2)).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransferWithDifferentCurrency_success() {
//        TransferRequest transferRequest = new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.ONE, Currency.getInstance(Locale.US)));
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(mockWallet);
//        when(userService.findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID)).thenReturn(mockWallet);
//        when(mockWallet.convertAmountToReceiverWalletCurrency(any(Money.class))).thenReturn(mockMoney);
//        when(mockWallet.calculateServiceFee(any(Money.class))).thenReturn(mockMoney);
//        when(mockMoney.add(any(Money.class))).thenReturn(mockMoney);
//        doNothing().when(mockWallet).withdraw(any(Money.class));
//        doNothing().when(mockWallet).deposit(any(Money.class));
//        doNothing().when(transactionService).createAndSaveTransaction(any(Wallet.class), any(Wallet.class), any(Money.class), any(Money.class));
//        when(walletRepository.save(any(Wallet.class))).thenReturn(mockWallet);
//
//        walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest);
//
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(1)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(walletRepository, times(2)).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void testTransferWithSameCurrency_InsufficientBalance_throwsInsufficientFundsException() {
//        TransferRequest transferRequest = new TransferRequest(VALID_WALLET_ID, RECIPIENT_USERNAME, new Money(BigDecimal.valueOf(150), Currency.getInstance(Locale.CANADA)));
//        when(userService.findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(senderWallet);
//        when(userService.findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID)).thenReturn(recipientWallet);
//
//        assertThrows(InsufficientFundsException.class, () -> walletService.transfer(VALID_WALLET_ID, SENDER_USERNAME, transferRequest));
//
//        verify(userService, times(1)).findTargetWallet(SENDER_USERNAME, VALID_WALLET_ID);
//        verify(userService, times(1)).findTargetWallet(RECIPIENT_USERNAME, VALID_WALLET_ID);
//        verify(walletRepository, never()).save(any(Wallet.class));
//        verify(userRepository, never()).save(any(User.class));
//    }
}