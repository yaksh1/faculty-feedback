package com.yaksh.user_security.repository;


import com.yaksh.user_security.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {
    Optional<OurUsers> findByEmail(String email);

    Optional<OurUsers> findByVerificationCode(String code);
}
