package com.swiggy.Wallet.entity;

import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private final Money money = new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.CANADA));
    private final Money anotherMoney = new Money(BigDecimal.valueOf(30), Currency.getInstance(Locale.CANADA));

    @Test
    void testAdd_validMoneyObjects_returnsNewObjectWithSum() {
        Money expectedMoney = new Money(BigDecimal.valueOf(80), Currency.getInstance(Locale.CANADA));

        Money actualMoney = money.add(anotherMoney);

        assertTrue(actualMoney.equals(expectedMoney));
    }

    @Test
    void testAdd_differentCurrencies_throwsCurrencyMismatchException() {
        Money moneyWithDifferentCurrency = new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.US));

        assertThrows(CurrencyMismatchException.class, () -> money.add(moneyWithDifferentCurrency));
    }

    @Test
    void testAdd_nullMoney_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.add(null));
    }

    @Test
    void testAdd_nullAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.add(new Money(null, Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testAdd_nullCurrency_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.add(new Money(BigDecimal.valueOf(50), null)));
    }

    @Test
    void testAdd_negativeAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.add(new Money(BigDecimal.valueOf(-50), Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testAdd_zeroAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.add(new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testSubtract_validMoneyObjects_returnsNewObjectWithDifference() {
        Money expectedMoney = new Money(BigDecimal.valueOf(20), Currency.getInstance(Locale.CANADA));

        Money actualMoney = money.subtract(anotherMoney);

        assertTrue(actualMoney.equals(expectedMoney));
    }

    @Test
    void testSubtract_differentCurrencies_throwsCurrencyMismatchException() {
        Money moneyWithDifferentCurrency = new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.US));

        assertThrows(CurrencyMismatchException.class, () -> money.subtract(moneyWithDifferentCurrency));
    }

    @Test
    void testSubtract_nullMoney_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.subtract(null));
    }

    @Test
    void testSubtract_nullAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.subtract(new Money(null, Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testSubtract_nullCurrency_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.subtract(new Money(BigDecimal.valueOf(50), null)));
    }

    @Test
    void testSubtract_negativeAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.subtract(new Money(BigDecimal.valueOf(-50), Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testSubtract_zeroAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> money.subtract(new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA))));
    }

    @Test
    void testIsNegative_negativeAmount_returnsTrue() {
        Money money = new Money(BigDecimal.valueOf(-50), Currency.getInstance(Locale.CANADA));

        assertTrue(money.isNegative());
    }

    @Test
    void testIsNegative_positiveAmount_returnsFalse() {
        assertFalse(money.isNegative());
    }

    @Test
    void testIsGreaterThan_greaterAmount_returnsTrue() {
        assertTrue(money.isGreaterThan(anotherMoney));
    }

    @Test
    void testIsGreaterThan_smallerAmount_returnsFalse() {
        assertFalse(anotherMoney.isGreaterThan(money));
    }

    @Test
    void testIsGreaterThan_differentCurrencies_throwsCurrencyMismatchException() {
        Money moneyWithDifferentCurrency = new Money(BigDecimal.valueOf(50), Currency.getInstance(Locale.US));

        assertThrows(CurrencyMismatchException.class, () -> money.isGreaterThan(moneyWithDifferentCurrency));
    }
}