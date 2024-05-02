package com.swiggy.Wallet.entity;

import com.swiggy.Wallet.Converter;
import com.swiggy.Wallet.Exceptions.InsufficientFundsException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Entity
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Money balance;

    public Wallet(Long id, Money balance) {
        validateMoney(balance);
        this.id = id;
        this.balance = balance;
    }

    public Wallet(Money balance) {
        validateMoney(balance);
        this.balance = balance;
    }

    public void deposit(Money amount) {
        validateMoney(amount);
        this.balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        validateAmount(amount);
        this.balance = balance.subtract(amount);
    }

    private void validateMoney(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.isNegative()) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateAmount(Money amount) {
        validateMoney(amount);
        if (amount.isGreaterThan(balance)) {
            throw new InsufficientFundsException("Insufficient funds to withdraw");
        }
    }

    public Money convertAmountToReceiverWalletCurrency(Money amount) {
        Currency baseCurrency = getBalance().getCurrency();
        Currency sourceCurrency = amount.getCurrency();

        if (sourceCurrency == null) {
            throw new IllegalArgumentException("Currency cannot be null, cannot process request");
        }

        if (!sourceCurrency.equals(baseCurrency)) {
            amount = Converter.convert(amount.getAmount().doubleValue(), baseCurrency, sourceCurrency);
        }

        return amount;
    }

    public Money calculateServiceFee(Money transferMoney) {
        Currency baseCurrency = getBalance().getCurrency();
        Currency sourceCurrency = transferMoney.getCurrency();
        Money serviceFee = new Money(BigDecimal.ZERO, sourceCurrency);

        if (!baseCurrency.equals(sourceCurrency)) {
            serviceFee = convertAmountToReceiverWalletCurrency(new Money(BigDecimal.TEN, Currency.getInstance("INR")));
        }

        return serviceFee;
    }
}
