package com.swiggy.Wallet.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.Wallet.Config.SecurityConfig;
import com.swiggy.Wallet.DTO.WalletTransactionRequest;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import com.swiggy.Wallet.entity.Money;
import com.swiggy.Wallet.entity.Wallet;
import com.swiggy.Wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @MockBean
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Autowired
    private MockMvc mockMvc;

    private final Wallet wallet = new Wallet(new Money(BigDecimal.TEN, Currency.getInstance("USD")));

    private final WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(amount);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void testCreate_whenAuthorizedUser_shouldReturnWallet() throws Exception {
        when(walletService.create(anyString())).thenReturn(wallet);

        mockMvc.perform(post("/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wallet created successfully for user: user"))
                .andExpect(jsonPath("$.statusCode").value(201));

        verify(walletService, times(1)).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithAnonymousUser
    void testCreate_whenUnauthorizedUser_returnsIsUnauthorizedError() throws Exception {
        mockMvc.perform(post("/wallets"))
                .andExpect(status().isUnauthorized());

        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithMockUser
    public void testCreate_userNotFound_returnsNotFound() throws Exception {
        when(walletService.create(anyString())).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/wallets"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(walletService, times(1)).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testCreate_unknownRepositoryError_returnsInternalServerError() throws Exception {
        when(walletService.create(anyString())).thenThrow(new RuntimeException("Unknown repository error"));

        mockMvc.perform(post("/wallets"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unknown repository error"))
                .andExpect(jsonPath("$.statusCode").value(500));

        verify(walletService, times(1)).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    void testFetchAllForAuthenticatedUser_shouldReturnListOfWallets_success() throws Exception {
        Wallet newWallet = new Wallet(new Money(new BigDecimal(20), Currency.getInstance("USD")));
        Set<Wallet> wallets = new HashSet<>(Set.of(wallet, newWallet));
        List<Wallet> walletList = new ArrayList<>(wallets);
        when(walletService.fetchAllFor(anyString())).thenReturn(wallets);

        mockMvc.perform(get("/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallets", hasSize(2)))
                .andExpect(jsonPath("$.wallets[0].id").value(walletList.get(0).getId()))
                .andExpect(jsonPath("$.wallets[0].balance.amount").value(walletList.get(0).getBalance().getAmount()))
                .andExpect(jsonPath("$.wallets[0].balance.currency").value(walletList.get(0).getBalance().getCurrency().getCurrencyCode()))
                .andExpect(jsonPath("$.wallets[1].id").value(walletList.get(1).getId()))
                .andExpect(jsonPath("$.wallets[1].balance.amount").value(walletList.get(1).getBalance().getAmount()))
                .andExpect(jsonPath("$.wallets[1].balance.currency").value(walletList.get(1).getBalance().getCurrency().getCurrencyCode()));

        verify(walletService, times(1)).fetchAllFor(anyString());
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithAnonymousUser
    void testFetchAllForUnauthenticatedUser_shouldReturnListOfWallets_success() throws Exception {
        mockMvc.perform(get("/wallets"))
                .andExpect(status().isUnauthorized());

        verify(walletService, never()).fetchAllFor(anyString());
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithMockUser
    public void testFetchAllForUser_userNotFound_returnsNotFound() throws Exception {
        when(walletService.fetchAllFor(anyString())).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/wallets"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(walletService, times(1)).fetchAllFor(anyString());
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithMockUser
    void testFetchAllForUser_whenUnexpectedError_returnsInternalServerError() throws Exception {
        when(walletService.fetchAllFor(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/wallets"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.statusCode").value(500));

        verify(walletService, times(1)).fetchAllFor(anyString());
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), anyString());
    }

    @Test
    @WithMockUser
    void testDeposit_whenAuthorizedUser_success() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT)).thenReturn(wallet);

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", DEPOSIT)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet.id").value(wallet.getId()))
                .andExpect(jsonPath("$.wallet.balance.amount").value(wallet.getBalance().getAmount()))
                .andExpect(jsonPath("$.wallet.balance.currency").value(wallet.getBalance().getCurrency().getCurrencyCode()))
                .andExpect(jsonPath("$.message").value("Deposit successful for user: user"))
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(walletService, times(1)).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testDeposit_whenDifferentCurrency_catchesCurrencyMismatchException_returnsBadRequest() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT))
                .thenThrow(new CurrencyMismatchException("Currency does not match with base currency"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", DEPOSIT)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency does not match with base currency"))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(walletService, times(1)).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testDeposit_whenUserNotFound_catchesUsernameNotFoundException_returnsNotFound() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", DEPOSIT)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(walletService, times(1)).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testDeposit_whenInvalidAmount_catchesIllegalArgumentException_returnsBadRequest() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT))
                .thenThrow(new IllegalArgumentException("Amount must be greater than zero"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", DEPOSIT)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(walletService, times(1)).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testDeposit_whenUnKnownError_returnsInternalServerError() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT)).thenThrow(new RuntimeException("Unexpected repository error"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", DEPOSIT)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected repository error"))
                .andExpect(jsonPath("$.statusCode").value(500));

        verify(walletService, times(1)).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithAnonymousUser
    public void testDeposit_whenUnauthorizedUser_returnsIsUnauthorized() throws Exception {
        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .header("Transaction-Type", DEPOSIT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), DEPOSIT);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testWithdraw_whenAuthorizedUser_shouldReturnWallet_success() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW)).thenReturn(wallet);

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", WITHDRAW)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Withdrawal successful for user: user"))
                .andExpect(jsonPath("$.statusCode").value(200));

        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testWithdraw_whenInsufficientBalance_cachesInsufficientFundsException_returnsBadRequest() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW))
                .thenThrow(new InsufficientFundsException("Insufficient balance in account, can't withdraw"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", WITHDRAW)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance in account, can't withdraw"))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }

    @Test
    @WithMockUser
    public void testTransfer_withWrongTransactionTypeInHeader_cachesUnsupportedOperationException_() throws Exception {
        when(walletService.performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW))
                .thenThrow(new UnsupportedOperationException("Operation on the wallet is not supported"));

        mockMvc.perform(post("/wallets/{id}", VALID_WALLET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Transaction-Type", INVALID_HEADER)
                        .content(objectMapper.writeValueAsString(walletTransactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Operation on the wallet is not supported"))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(walletService, never()).performWalletTransaction(anyLong(), anyString(), any(WalletTransactionRequest.class), WITHDRAW);
        verify(walletService, never()).create(anyString());
        verify(walletService, never()).fetchAllFor(anyString());
    }
}