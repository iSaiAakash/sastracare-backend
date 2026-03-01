package com.academic.sastracare.repository;

import com.academic.sastracare.entity.Parent;
import com.academic.sastracare.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByParentId(String parentId);

    Optional<Student> findByIdAndParentId(Long studentId, String parentId);

    boolean existsByRegisterNumber(String number);

    List<Student> findByParent(Parent parent);
}