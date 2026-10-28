package com.cashbook.cashbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CashbookRepository extends JpaRepository<Cashbook, Long> {

    List<Cashbook> findByBusinessId(Long businessId);

    boolean existsByNameAndBusinessId(String name, Long businessId);


    List<Cashbook> findByBusinessIdIn(Collection<Long> businessIds);


    @Query("""
        SELECT DISTINCT cm.cashbook
        FROM CashbookMember cm
        WHERE cm.user.id = :userId
    """)
    List<Cashbook> findSharedWithUser(@Param("userId") Long userId);

}
