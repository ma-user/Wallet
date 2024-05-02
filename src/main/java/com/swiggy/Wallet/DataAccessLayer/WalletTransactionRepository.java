package com.swiggy.Wallet.DataAccessLayer;

import com.swiggy.Wallet.entity.Transaction;
import com.swiggy.Wallet.entity.Wallet;
import com.swiggy.Wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findAllByWalletOrderByTimestampDesc(Wallet wallet);

    List<WalletTransaction> findAllByWalletAndTimestampBetween(Wallet wallet, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
