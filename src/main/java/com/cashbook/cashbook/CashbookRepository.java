package com.cashbook.cashbook;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbookRepository extends JpaRepository<Cashbook, Long> {

    List<Cashbook> findByBusinessId(Long businessId);

    boolean existsByNameAndBusinessId(String name, Long businessId);
}
