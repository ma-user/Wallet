package com.swiggy.Wallet.entity;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import static com.swiggy.Wallet.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final static String CITY = "Indore";
    private final static String COUNTRY = "Canada";
    private final static Location LOCATION = new Location(CITY, COUNTRY);

    @Test
    public void testInitializeUser_withEmptyUsernameValidPasswordValidLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(Strings.EMPTY, PASSWORD, LOCATION));
    }

    @Test
    public void testInitializeUser_withValidUsernameEmptyPasswordValidLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(USERNAME, Strings.EMPTY, LOCATION));
    }

    @Test
    public void testInitializeUser_withValidUsernameValidPasswordNullLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(USERNAME, PASSWORD, (Location) null));
    }

    @Test
    public void testInitializeUser_withValidUsernameValidPasswordEmptyCountry_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(USERNAME, PASSWORD, new Location(CITY, Strings.EMPTY)));
    }

    @Test
    public void testInitializeUser_withValidUsernameValidPasswordInValidLocation_success() {
        Wallet expectedWallet = new Wallet(new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA)));

        User user = new User(USERNAME, PASSWORD, LOCATION);

        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(LOCATION, user.getLocation());
        assertEquals(1, user.getWallets().size());
        assertTrue(user.getWallets().contains(expectedWallet));
    }

    @Test
    public void testInitializeUser_withValidUsernameValidPasswordInvalidLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(USERNAME, PASSWORD, new Location(CITY, INVALID_COUNTRY)));
    }

    @Test
    public void testInitializeUser_withNullSetOfWallets_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new User(USERNAME, PASSWORD, (Set<Wallet>) null));
    }

    @Test
    public void testInitializeUser_withSpecificSetOfWallet_success() {
        Wallet wallet = new Wallet(new Money(BigDecimal.ZERO, Currency.getInstance(Locale.CANADA)));

        User user = new User(USERNAME, PASSWORD, Set.of(wallet));

        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(1, user.getWallets().size());
        assertTrue(user.getWallets().contains(wallet));
    }
}