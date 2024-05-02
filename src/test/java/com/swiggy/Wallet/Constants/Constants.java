package com.swiggy.Wallet.Constants;

import com.swiggy.Wallet.entity.Location;
import com.swiggy.Wallet.entity.Money;
import com.swiggy.Wallet.entity.User;
import com.swiggy.Wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class Constants {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SENDER_USERNAME = "sender";
    public static final String RECIPIENT_USERNAME = "recipient";
    public static final String SENDER_PASSWORD = "sender_password";
    public static final String RECIPIENT_PASSWORD = "recipient_password";
    public static final String ENCODED_PASSWORD = "encoded_password";
    public static final String INVALID_PASSWORD = "short";
    public static final Long VALID_WALLET_ID = 1L;
    public static final Long ANOTHER_VALID_WALLET_ID = 2L;
    public static final Long INVALID_WALLET_ID = 2L;
    public static final Long VALID_USER_ID = 3L;
    public static final Long INVALID_USER_ID = 4L;
    public static final String CITY = "city";
    public static final String VALID_COUNTRY = "CANADA";
    public static final String INVALID_COUNTRY = "INVALID_COUNTRY";
    public static final Location VALID_LOCATION = new Location(CITY, VALID_COUNTRY);
    public static final Location INVALID_LOCATION = new Location(CITY, INVALID_COUNTRY);

    public static final Wallet wallet = new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));
    public static final Wallet anotherWallet = new Wallet(ANOTHER_VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)));
    public static final User user = new User(VALID_USER_ID, SENDER_USERNAME, SENDER_PASSWORD, Set.of(new Wallet(VALID_WALLET_ID, new Money(BigDecimal.valueOf(100), Currency.getInstance(Locale.CANADA)))), VALID_LOCATION);
    public static final Money amount = new Money(BigDecimal.valueOf(10), Currency.getInstance("USD"));
    public static final Money anotherAmount = new Money(BigDecimal.valueOf(20), Currency.getInstance("USD"));
    public static final String DEPOSIT = "deposit";
    public static final String WITHDRAW = "withdraw";
    public static final String INVALID_HEADER = "invalid";
}
