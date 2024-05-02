package com.swiggy.Wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiggy.Wallet.Utils.LocationUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<Wallet> wallets = new HashSet<>();

    private Location location;

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Location location) {
        validateUsernamePassword(username, password);
        validateLocation(location);
        this.username = username;
        this.password = password;
        this.location = location;
        this.wallets.add(createWalletBasedOn(location));
    }

    public User(String username, String password, Set<Wallet> wallets) {
        validateUsernamePassword(username, password);
        validateWallet(wallets);
        this.username = username;
        this.password = password;
        this.wallets = wallets;
    }

    private Wallet createWalletBasedOn(Location location) {
        Currency defaultCurrency = LocationUtils.getDefaultCurrencyForLocation(location);
        Money initialBalance = new Money(BigDecimal.ZERO, defaultCurrency);
        return new Wallet(initialBalance);
    }

    private void validateUsernamePassword(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username or password cannot be empty");
        }
    }

    private void validateLocation(Location location) {
        if (location == null || location.getCountry().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
    }

    private void validateWallet(Set<Wallet> wallets) {
        if (wallets == null) {
            throw new IllegalArgumentException("List of Wallets cannot be null");
        }
    }

    public Wallet addWalletBasedOn(Location location) {
        Wallet newWallet = createWalletBasedOn(location);
        this.wallets.add(newWallet);
        return newWallet;
    }
}
