package com.swiggy.Wallet.Utils;

import com.swiggy.Wallet.entity.Location;

import java.util.Currency;

public class LocationUtils {
    public static Currency getDefaultCurrencyForLocation(Location location) {
        return switch (location.getCountry().toLowerCase()) {
            case "india" -> Currency.getInstance("INR");
            case "us" -> Currency.getInstance("USD");
            case "uk" -> Currency.getInstance("GBP");
            case "canada" -> Currency.getInstance("CAD");
            default -> throw new IllegalArgumentException("Unsupported location: " + location);
        };
    }
}
