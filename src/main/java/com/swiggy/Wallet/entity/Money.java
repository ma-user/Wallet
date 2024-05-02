package com.swiggy.Wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiggy.Wallet.Exceptions.CurrencyMismatchException;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    private BigDecimal amount;

    private Currency currency;

    public Money add(Money other) {
        validate(other);
        return new Money(amount.add(other.getAmount()), currency);
    }

    public Money subtract(Money other) {
        validate(other);
        return new Money(amount.subtract(other.getAmount()), currency);
    }

    @JsonIgnore
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThan(Money other) {
        validate(other);
        return amount.compareTo(other.getAmount()) > 0;
    }

    private void validate(Money other) {
        if (other == null || other.getAmount() == null || other.getCurrency() == null || other.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid money: " + other);
        }

        if (!currency.equals(other.getCurrency())) {
            throw new CurrencyMismatchException(other + " Currency does not match with base currency");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    public Money convertToBaseCurrency(Currency baseCurrency, Currency sourceCurrency) {
        BigDecimal depositedAmount = getAmount();

        if (depositedAmount == null || depositedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid Amount, cannot process request");
        }

        MonetaryAmount sourceMoney = Monetary.getDefaultAmountFactory().setCurrency(sourceCurrency.getCurrencyCode()).setNumber(depositedAmount).create();
        CurrencyConversion conversionCurrency = MonetaryConversions.getConversion(baseCurrency.getCurrencyCode());
        MonetaryAmount convertedSourceMoneyToBaseCurrency = sourceMoney.with(conversionCurrency);
        double convertedAmount = convertedSourceMoneyToBaseCurrency.getNumber().doubleValueExact();
        BigDecimal formattedValue = BigDecimal.valueOf(convertedAmount).setScale(2, RoundingMode.HALF_EVEN);

        return new Money(formattedValue, baseCurrency);
    }

    public void validate() {
        if (getCurrency() == null || getAmount() == null || isNegative()) {
            throw new IllegalArgumentException("Invalid money, cancelling request" + this);
        }
    }
}
