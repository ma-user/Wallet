package com.swiggy.Wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiverWallet;

    @Column(nullable = false)
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_currency"))
    })
    private Money amount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "service_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "service_fee_currency"))
    })
    private Money serviceFee;

    public Transaction(Wallet senderWallet, Wallet receiverWallet, Money amount, LocalDateTime timestamp, Money serviceFee) {
        validate(senderWallet, receiverWallet, amount, timestamp, serviceFee);
        this.senderWallet = senderWallet;
        this.receiverWallet = receiverWallet;
        this.amount = amount;
        this.timestamp = timestamp;
        this.serviceFee = serviceFee;
    }

    private void validate(Wallet senderWallet, Wallet receiverWallet, Money amount, LocalDateTime timestamp, Money serviceFee) {
        if (senderWallet == null || receiverWallet == null || amount == null || timestamp == null || serviceFee == null || senderWallet.equals(receiverWallet)) {
            throw new IllegalArgumentException("Invalid transaction from " + senderWallet + " to " + receiverWallet + " of amount " + amount + " on date " + timestamp);
        }
    }
}
