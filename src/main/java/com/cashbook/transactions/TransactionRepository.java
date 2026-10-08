package com.cashbook.transactions;

import com.cashbook.cashbook.Cashbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCashbookIdOrderByTransactionDateDesc(Long cashbookId);

    Optional<Transaction> findTopByCashbookOrderByIdDesc(Cashbook cashbook);

    boolean existsByTransactionNumber(String transactionNumber);
}