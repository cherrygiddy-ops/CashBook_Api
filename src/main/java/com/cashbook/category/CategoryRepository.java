package com.cashbook.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByBusinessId(Long businessId);

    boolean existsByNameAndBusinessId(String name, Long businessId);
}