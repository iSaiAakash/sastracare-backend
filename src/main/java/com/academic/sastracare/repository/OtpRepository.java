package com.academic.sastracare.repository;

import com.academic.sastracare.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByPhoneNumberOrderByIdDesc(String phoneNumber);

    void deleteByExpiryTimeBefore(LocalDateTime time);
}
