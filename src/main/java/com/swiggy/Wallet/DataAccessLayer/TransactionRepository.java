package com.swiggy.Wallet.DataAccessLayer;

import com.swiggy.Wallet.entity.Transaction;
import com.swiggy.Wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySenderWalletOrReceiverWalletOrderByTimestampDesc(Wallet senderWallet, Wallet receiverWallet);
    List<Transaction> findAllBySenderWalletAndTimestampBetweenOrReceiverWalletAndTimestampBetween(Wallet senderWallet, LocalDateTime startOfDaySender,
                                                                                                  LocalDateTime endOfDaySender, Wallet receiverWallet,
                                                                                                  LocalDateTime startOfDayReceiver, LocalDateTime endOfDayReceiver);

    List<Transaction> findByServiceFeeIsNull();
    List<Transaction> findByAmountIsNull();
}
