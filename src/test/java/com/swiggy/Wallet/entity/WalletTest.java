package com.swiggy.Wallet.entity;

import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testInitializeWalletWithNullBalance_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Wallet(null));
    }

    @Test
    void testInitializeWalletWithSpecificBalance_success() {
        Wallet wallet = new Wallet(new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA)));
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance().getAmount());
        assertEquals(Currency.getInstance(Locale.CANADA), wallet.getBalance().getCurrency());
    }

    @Test
    void testDeposit_negativeAmount_throwsIllegalArgumentException() {
        Money initialAmount = new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA));
        Money depositAmount = new Money(new BigDecimal(-100), Currency.getInstance(Locale.CANADA));
        Wallet wallet = new Wallet(initialAmount);

        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(depositAmount));
    }

    @Test
    void testDeposit_validAmount_balanceUpdated_sucess() {
        Money initialAmount = new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA));
        Money depositAmount = new Money(new BigDecimal(50), Currency.getInstance(Locale.CANADA));
        Wallet wallet = new Wallet(initialAmount);

        wallet.deposit(depositAmount);

        assertEquals(initialAmount.add(depositAmount), wallet.getBalance());
    }

    @Test
    void testWithdraw_negativeAmount_throwsIllegalArgumentException() {
        Money initialAmount = new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA));
        Money withdrawAmount = new Money(new BigDecimal(-50), Currency.getInstance(Locale.CANADA));
        Wallet wallet = new Wallet(initialAmount);

        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(withdrawAmount));
    }

    @Test
    void testWithdraw_insufficientFunds_throwsInsufficientFundsException() {
        Money initialAmount = new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA));
        Money withdrawAmount = new Money(new BigDecimal(150), Currency.getInstance(Locale.CANADA));
        Wallet wallet = new Wallet(initialAmount);

        assertThrows(InsufficientFundsException.class, () -> wallet.withdraw(withdrawAmount));
    }

    @Test
    void testWithdraw_validAmount_balanceUpdated_success() {
        Money initialAmount = new Money(new BigDecimal(100), Currency.getInstance(Locale.CANADA));
        Money withdrawAmount = new Money(new BigDecimal(50), Currency.getInstance(Locale.CANADA));
        Wallet wallet = new Wallet(initialAmount);

        wallet.withdraw(withdrawAmount);

        assertEquals(initialAmount.subtract(withdrawAmount), wallet.getBalance());
    }
}