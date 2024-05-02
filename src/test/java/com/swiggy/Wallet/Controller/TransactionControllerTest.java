//package com.swiggy.Wallet.Controller;
//
//import com.swiggy.Wallet.Config.SecurityConfig;
//import com.swiggy.Wallet.DTO.TransactionDTO;
//import com.swiggy.Wallet.Exceptions.InvalidAuthenticationException;
//import com.swiggy.Wallet.entity.Money;
//import com.swiggy.Wallet.service.TransactionService;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static com.swiggy.Wallet.Constants.Constants.*;
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@Import(SecurityConfig.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class TransactionControllerTest {
//
//    @MockBean
//    private TransactionService transactionService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @InjectMocks
//    private TransactionController transactionController;

//    private final TransferRequest transferRequest = new TransferRequest(VALID_WALLET_ID, USERNAME, amount);

//
//    public static final Money serviceFee = new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA));
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_ForValidWallet_success() throws Exception {
//        List<TransactionDTO> transactions = List.of(new TransactionDTO(VALID_WALLET_ID, ANOTHER_VALID_WALLET_ID, amount, serviceFee, LocalDateTime.now()),
//                new TransactionDTO(ANOTHER_VALID_WALLET_ID, VALID_WALLET_ID, anotherAmount, serviceFee, LocalDateTime.now()));
//        when(transactionService.fetchAllTransactionsForUserAndWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenReturn(transactions);
//
//        mockMvc.perform(get("/wallets/{id}/transactions", VALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Retrieved all transactions successfully for user " + SENDER_USERNAME + " with wallet " + VALID_WALLET_ID))
//                .andExpect(jsonPath("$.statusCode").value(200))
//                .andExpect(jsonPath("$.transactions", hasSize(2)));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_ForDifferentWallet_catchesNoSuchElementException_returnsNotFound() throws Exception {
//        when(transactionService.fetchAllTransactionsForUserAndWallet(anyString(), anyLong())).thenThrow(new NoSuchElementException("No wallet found for user"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions", INVALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("No wallet found for user"))
//                .andExpect(jsonPath("$.statusCode").value(404));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_ForInvalidUser_catchesInvalidAuthenticationException_returnsIsUnauthorized() throws Exception {
//        when(transactionService.fetchAllTransactionsForUserAndWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(new InvalidAuthenticationException("Invalid authentication"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions", VALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("Invalid authentication"))
//                .andExpect(jsonPath("$.statusCode").value(401));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_receivesNullListOfTransactions_returnsInternalServerError() throws Exception {
//        when(transactionService.fetchAllTransactionsForUserAndWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(new NullPointerException("Received null list of transactions"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions", VALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Received null list of transactions"))
//                .andExpect(jsonPath("$.statusCode").value(500));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_unknownErrorFromRepository_returnsInternalServerError() throws Exception {
//        when(transactionService.fetchAllTransactionsForUserAndWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(new RuntimeException("Unknown repository error"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions", VALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Unknown repository error"))
//                .andExpect(jsonPath("$.statusCode").value(500));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWallet_noUserFound_catchesUsernameNotFoundException_returnsNotFound() throws Exception {
//        when(transactionService.fetchAllTransactionsForUserAndWallet(SENDER_USERNAME, VALID_WALLET_ID)).thenThrow(new UsernameNotFoundException("User not found"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions", VALID_WALLET_ID)
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("User not found"))
//                .andExpect(jsonPath("$.statusCode").value(404));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWalletOnDate(anyString(), anyLong(), any(LocalDate.class));
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_success() throws Exception {
//        LocalDate date = LocalDate.now();
//        List<TransactionDTO> transactions = Arrays.asList(new TransactionDTO(), new TransactionDTO());
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date)).thenReturn(transactions);
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", VALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.transactions", hasSize(2)))
//                .andExpect(jsonPath("$.message").value("Retrieved all transactions successfully for user " + SENDER_USERNAME))
//                .andExpect(jsonPath("$.statusCode").value(200));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date);
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_catchesInvalidAuthenticationException_returnsIsUnauthorized() throws Exception {
//        LocalDate date = LocalDate.now();
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date)).thenThrow(new InvalidAuthenticationException("Invalid authentication"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", VALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("Invalid authentication"))
//                .andExpect(jsonPath("$.statusCode").value(401));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date);
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_nullListOfTransactions_returnsInternalServerError() throws Exception {
//        LocalDate date = LocalDate.now();
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date)).thenThrow(new NullPointerException("List of transactions is null"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", VALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("List of transactions is null"))
//                .andExpect(jsonPath("$.statusCode").value(500));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date);
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_unknownErrorFromRepository() throws Exception {
//        LocalDate date = LocalDate.now();
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date)).thenThrow(new RuntimeException("Unknown repository error"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", VALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Unknown repository error"))
//                .andExpect(jsonPath("$.statusCode").value(500));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date);
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_noUserFound_catchesUsernameNotFoundException_returnsNotFound() throws Exception {
//        LocalDate date = LocalDate.now();
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date)).thenThrow(new UsernameNotFoundException("User not found"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", VALID_WALLET_ID, VALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("User not found"))
//                .andExpect(jsonPath("$.statusCode").value(404));
//
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, VALID_WALLET_ID, date);
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//    }
//
//    @Test
//    public void testFetchAllTransactionsForUserAndWalletOnDate_ForDifferentWallet_catchesNoSuchElementException_returnsNotFound() throws Exception {
//        LocalDate date = LocalDate.now();
//        when(transactionService.fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, INVALID_WALLET_ID, date)).thenThrow(new NoSuchElementException("No wallet found for user"));
//
//        mockMvc.perform(get("/wallets/{id}/transactions/filter", INVALID_WALLET_ID)
//                        .param("date", date.toString())
//                        .with(user(SENDER_USERNAME))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("No wallet found for user"))
//                .andExpect(jsonPath("$.statusCode").value(404));
//
//        verify(transactionService, times(0)).fetchAllTransactionsForUserAndWallet(anyString(), anyLong());
//        verify(transactionService, times(1)).fetchAllTransactionsForUserAndWalletOnDate(SENDER_USERNAME, INVALID_WALLET_ID, date);
//    }

//    @Test
//    @WithMockUser
//    void testTransfer_whenAuthorizedUser_success() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class))).thenReturn(wallet);
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.wallet.id").value(wallet.getId()))
//                .andExpect(jsonPath("$.wallet.balance.amount").value(wallet.getBalance().getAmount()))
//                .andExpect(jsonPath("$.wallet.balance.currency").value(wallet.getBalance().getCurrency().getCurrencyCode()))
//                .andExpect(jsonPath("$.message").value("Money transferred successfully for user: user"))
//                .andExpect(jsonPath("$.statusCode").value(200));
//
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletTransactionRequestDTO.class));
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletTransactionRequestDTO.class));
//    }
//
//    @Test
//    @WithMockUser
//    public void testTransfer_WithDifferentCurrency_catchesCurrencyMismatchException_returnsBadRequest() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class)))
//                .thenThrow(new CurrencyMismatchException("Currency does not match with base currency"));
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Currency does not match with base currency"))
//                .andExpect(jsonPath("$.statusCode").value(400));
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletTransactionRequestDTO.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletTransactionRequestDTO.class));
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//
//    @Test
//    @WithMockUser
//    public void testTransfer_WithInsufficientBalance_catchesInsufficientFundsException_returnsBadRequest() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class)))
//                .thenThrow(new InsufficientFundsException("Insufficient balance"));
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Insufficient balance"))
//                .andExpect(jsonPath("$.statusCode").value(400));
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletDepositRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletWithdrawRequest.class));
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//
//    @Test
//    @WithMockUser
//    public void testTransfer_withInvalidAmount_catchesIllegalArgumentException_returnsBadRequest() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class)))
//                .thenThrow(new IllegalArgumentException("Amount must be greater than zero"));
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"))
//                .andExpect(jsonPath("$.statusCode").value(400));
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletDepositRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletWithdrawRequest.class));
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//
//    @Test
//    @WithMockUser
//    public void testTransfer_noUserFound_returnsNotFound() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class))).thenThrow(new UsernameNotFoundException("User not found"));
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("User not found"))
//                .andExpect(jsonPath("$.statusCode").value(404));
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletDepositRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletWithdrawRequest.class));
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//
//    @Test
//    @WithMockUser
//    public void testTransfer_whenUnKnownError_returnsInternalServerError() throws Exception {
//        when(walletService.transfer(anyLong(), anyString(), any(TransferRequest.class))).thenThrow(new RuntimeException("Unexpected repository error"));
//
//        mockMvc.perform(post("/wallets/{id}/transfer", VALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Unexpected repository error"))
//                .andExpect(jsonPath("$.statusCode").value(500));
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletDepositRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletWithdrawRequest.class));
//        verify(walletService, times(1)).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//
//    @Test
//    @WithAnonymousUser
//    public void testTransfer_whenUnauthorizedUser_returnsIsUnauthorized() throws Exception {
//        mockMvc.perform(post("/wallets/{id}/transfer", INVALID_WALLET_ID)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//
//        verify(walletService, never()).deposit(anyLong(), anyString(), any(WalletDepositRequest.class));
//        verify(walletService, never()).create(anyString());
//        verify(walletService, never()).fetchAllFor(anyString());
//        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletWithdrawRequest.class));
//        verify(walletService, never()).transfer(anyLong(), anyString(), any(TransferRequest.class));
//    }
//}