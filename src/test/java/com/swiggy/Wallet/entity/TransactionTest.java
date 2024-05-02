package com.swiggy.Wallet.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    private final Wallet senderWallet = new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));
    private final Wallet receiverWallet = new Wallet(ANOTHER_VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Money serviceFee = new Money(BigDecimal.TEN, Currency.getInstance(Locale.CANADA));

    @Test
    public void testCreateTransactionWithNullSender_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(null, receiverWallet, amount, timestamp, serviceFee));
    }

    @Test
    public void testCreateTransactionWithNullReceiver_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(senderWallet, null, amount, timestamp, serviceFee));
    }

    @Test
    public void testCreateTransactionWithNullAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(senderWallet, receiverWallet, null, timestamp, serviceFee));
    }

    @Test
    public void testCreateTransactionWithNullTimestamp_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(senderWallet, receiverWallet, amount, null, serviceFee));
    }

    @Test
    public void testCreateTransactionWithSameSenderAndReceiver_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(senderWallet, senderWallet, amount, timestamp, serviceFee));
    }

    @Test
    public void testCreateTransactionWithNullServiceFee_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(senderWallet, receiverWallet, amount, timestamp, null));
    }

    @Test
    public void testCreateTransaction_success() {
        Transaction transaction = new Transaction(senderWallet, receiverWallet, amount, timestamp, serviceFee);

        assertEquals(senderWallet, transaction.getSenderWallet());
        assertEquals(receiverWallet, transaction.getReceiverWallet());
        assertEquals(amount, transaction.getAmount());
        assertEquals(timestamp, transaction.getTimestamp());
    }
}