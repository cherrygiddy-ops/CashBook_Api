package com.cashbook.auth.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByVerificationToken(String token);

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    Long countAllUsers();

    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = 1", nativeQuery = true)
    Long countActiveUsers();
}
