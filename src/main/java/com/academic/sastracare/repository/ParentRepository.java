package com.academic.sastracare.repository;

import com.academic.sastracare.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, String> {

    Optional<Parent> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
}