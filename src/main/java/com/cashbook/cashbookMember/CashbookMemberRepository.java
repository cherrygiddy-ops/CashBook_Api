package com.cashbook.cashbookMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashbookMemberRepository
        extends JpaRepository<CashbookMember, Long> {

    boolean existsByCashbook_IdAndUser_Id(
            Long cashbookId,
            Long userId
    );

    Optional<CashbookMember> findByCashbook_IdAndUser_Id(
            Long cashbookId,
            Long userId
    );

    List<CashbookMember> findAllByCashbook_Id(Long cashbookId);

    List<CashbookMember> findAllByUser_Id(Long userId);

    void deleteByCashbook_IdAndUser_Id(
            Long cashbookId,
            Long userId
    );

    List<CashbookMember> findByUser_Id(Long userId);

}