package com.cashbook.businessMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessMemberRepository
        extends JpaRepository<BusinessMember, Long> {

    Optional<BusinessMember> findByBusinessIdAndCashbookuserId(
            Long businessId,
            Long userId
    );

    boolean existsByBusinessIdAndCashbookuserId(
            Long businessId,
            Long userId
    );

    List<BusinessMember> findByBusinessId(Long businessId);
}