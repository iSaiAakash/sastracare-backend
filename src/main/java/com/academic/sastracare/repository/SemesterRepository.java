package com.academic.sastracare.repository;

import com.academic.sastracare.entity.Semester;
import com.academic.sastracare.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // Fetch the explicitly active semester
    Optional<Semester> findByStudentAndActiveTrue(Student student);

    List<Semester> findByStudent(Student student);

    Optional<Semester> findByStudentAndSemesterNumber(
            Student student,
            Integer semesterNumber
    );

}