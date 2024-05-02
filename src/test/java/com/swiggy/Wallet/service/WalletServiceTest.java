package com.swiggy.Wallet.service;

import com.swiggy.Wallet.DTO.WalletTransactionRequest;
import com.swiggy.Wallet.DataAccessLayer.UserRepository;
import com.swiggy.Wallet.DataAccessLayer.WalletRepository;
import com.swiggy.Wallet.DataAccessLayer.WalletTransactionRepository;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.DataAccessException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.entity.*;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.*;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Wallet mockWallet;

    @Mock
    private Money mockMoney;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private WalletTransaction mockWalletTransaction;

    @InjectMocks
    private WalletService walletService;

    private final WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.CANADA)));

    public final User user = new User(VALID_USER_ID, USERNAME, PASSWORD, new HashSet<>(Set.of(new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA))))), VALID_LOCATION);

    private final Wallet wallet = new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));

    @Test
    public void testCreate_UserNotFound_throwsUsernameNotFoundException() {
        when(userService.findByUsername(any())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> walletService.create(USERNAME));

        verify(userService, times(1)).findByUsername(USERNAME);
        verify(userRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    public void testCreate_UnknownRepositoryError_throwsDataAccessException() {
        when(userService.findByUsername(USERNAME)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataAccessException.class);

        assertThrows(DataAccessException.class, () -> walletService.create(USERNAME));

        verify(userService, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    public void testCreate_success() {
        Wallet expectedWallet = new Wallet(new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA)));
        when(userService.findByUsername(USERNAME)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        Wallet createdWallet = walletService.create(USERNAME);

        assertNotNull(createdWallet);
        assertEquals(expectedWallet, createdWallet);
        verify(userService, times(1)).findByUsername(USERNAME);
        verify(userRepository, times(1)).save(user);
        verify(walletRepository, never()).save(any());
    }

    @Test
    public void testFetchAllWalletsForUser_success() {
        when(userService.fetchWallets(USERNAME)).thenReturn(Set.of(mockWallet));

        walletService.fetchAllFor(USERNAME);

        verify(userService, times(1)).fetchWallets(USERNAME);
        verify(walletRepository, never()).save(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFetchAllWalletsForUser_unknownRepositoryError_throwsDataAccessException() {
        when(userService.fetchWallets(USERNAME)).thenThrow(RuntimeException.class);

        assertThrows(DataAccessException.class, () -> walletService.fetchAllFor(USERNAME));

        verify(userService, times(1)).fetchWallets(USERNAME);
        verify(walletRepository, never()).save(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFetchAllWalletsForUser_noUserFound_throwsUsernameNotFoundException() {
        when(userService.fetchWallets(USERNAME)).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> walletService.fetchAllFor(USERNAME));

        verify(userService, times(1)).fetchWallets(USERNAME);
        verify(walletRepository, never()).save(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithValidAmountDifferentCurrency_ConversionUnsuccessful_throwsCurrencyMismatchException() {
        when(userService.findTargetWallet(anyString(), anyLong())).thenReturn(mockWallet);
        when(mockWallet.convertAmountToReceiverWalletCurrency(any(Money.class))).thenReturn(mockMoney);
        doThrow(CurrencyMismatchException.class).when(mockWallet).deposit(any(Money.class));

        assertThrows(CurrencyMismatchException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithValidAmountNullCurrency_throwsIllegalArgumentException() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(new Money(BigDecimal.TEN, null));

        assertThrows(IllegalArgumentException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, times(0)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithNullAmountDifferentCurrency_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, new WalletTransactionRequest(new Money(null, Currency.getInstance(Locale.US))), DEPOSIT));

        verify(userService, never()).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithNegativeAmountSameCurrency_throwsIllegalArgumentException() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(new Money(BigDecimal.valueOf(-50), Currency.getInstance(Locale.CANADA)));

        assertThrows(IllegalArgumentException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, never()).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, never()).save(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithValidAmountDifferentCurrency_ConversionSuccessful_DepositSuccess() {
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenReturn(mockWallet);
        when(mockWallet.convertAmountToReceiverWalletCurrency(any())).thenReturn(mockMoney);
        doNothing().when(mockWallet).deposit(mockMoney);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(mockWalletTransaction);
        when(walletRepository.save(mockWallet)).thenReturn(mockWallet);

        walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT);

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletTransactionRepository, times(1)).save(any(WalletTransaction.class));
        verify(walletRepository, times(1)).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeposit_unknownRepositoryError_throwsDataAccessException() {
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenThrow(RuntimeException.class);

        assertThrows(DataAccessException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletTransactionRepository, never()).save(any(WalletTransaction.class));
        verify(walletRepository, never()).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeposit_noUserFound_throwsUsernameNotFoundException() {
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletTransactionRepository, never()).save(any(WalletTransaction.class));
        verify(walletRepository, never()).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeposit_noWalletFound_throwsNoSuchElementException() {
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT));

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletTransactionRepository, never()).save(any(WalletTransaction.class));
        verify(walletRepository, never()).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositWithValidAmountSameCurrency_successfulDeposit() {
        Wallet expectedWallet = new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(150), Currency.getInstance(Locale.CANADA)));
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(mockWalletTransaction);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet actualWallet = walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, DEPOSIT);

        assertEquals(expectedWallet, actualWallet);
        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletTransactionRepository, times(1)).save(any(WalletTransaction.class));
        verify(walletRepository, times(1)).save(wallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testWithdrawWithDifferentCurrencyValidAmount_ConversionSuccess_WithdrawSuccess() {
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenReturn(mockWallet);
        when(mockWallet.convertAmountToReceiverWalletCurrency(any(Money.class))).thenReturn(mockMoney);
        doNothing().when(mockWallet).withdraw(mockMoney);
        when(walletRepository.save(mockWallet)).thenReturn(mockWallet);

        walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, WITHDRAW);

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, times(1)).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testWithdrawWithInsufficientFunds_throwsInsufficientFundsException() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(new Money(BigDecimal.valueOf(150), Currency.getInstance(Locale.CANADA)));
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenReturn(wallet);

        assertThrows(InsufficientFundsException.class, () -> walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, WITHDRAW));

        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, never()).save(mockWallet);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testWithdrawWithSameCurrencyValidAmount_WithdrawSuccess() {
        Wallet expectedWallet = new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.CANADA)));
        when(userService.findTargetWallet(USERNAME, VALID_WALLET_ID)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet actualwallet = walletService.performWalletTransaction(VALID_WALLET_ID, USERNAME, walletTransactionRequest, WITHDRAW);

        assertEquals(expectedWallet, actualwallet);
        verify(userService, times(1)).findTargetWallet(USERNAME, VALID_WALLET_ID);
        verify(walletRepository, times(1)).save(wallet);
        verify(userRepository, never()).save(any(User.class));
    }
}