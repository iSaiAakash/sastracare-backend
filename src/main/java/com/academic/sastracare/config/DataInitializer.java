package com.academic.sastracare.config;

import com.academic.sastracare.entity.*;
import com.academic.sastracare.repository.ParentRepository;
import com.academic.sastracare.repository.SemesterRepository;
import com.academic.sastracare.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    @Override
    public void run(String... args) {

        if (parentRepository.existsByMobile("9876543210")) {
            System.out.println("Sample data already exists. Skipping initialization.");
            return;
        }
        // =========================
        // 1️⃣ CREATE PARENT
        // =========================
        Parent parent = new Parent();
        parent.setMobile("9876543210");
        parent.setActive(true);
        parent.setMobileVerified(true);
        parent.setName("Ramesh Kumar");

        parentRepository.save(parent);

        // =========================
        // 2️⃣ CREATE TWO STUDENTS
        // =========================
        Student student1 = new Student();
        student1.setName("Arjun");
        student1.setCgpa(8.5);
        student1.setRegisterNumber("226027001");
        student1.setProgram("CSE");
        student1.setParent(parent);
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("Karthik");
        student2.setCgpa(8.2);
        student2.setRegisterNumber("226027002");
        student2.setProgram("CSE");
        student2.setParent(parent);
        studentRepository.save(student2);

        // =========================
        // 3️⃣ CREATE SEMESTERS
        // =========================
        createSemestersForStudent(student1);
        createSemestersForStudent(student2);

        System.out.println("✅ Sample academic data inserted successfully.");
    }

    private void createSemestersForStudent(Student student) {

        for (int sem = 1; sem <= 5; sem++) {

            Semester semester = new Semester();
            semester.setSemesterNumber(sem);
            semester.setSgpa(7.5 + (sem * 0.2));
            semester.setActive(sem == 5);
            semester.setStudent(student);

            // Attendance
            Attendance attendance = new Attendance();
            attendance.setTotalDays(90);
            attendance.setPresentDays(80 + sem);
            attendance.setSemester(semester);
            semester.setAttendance(attendance);

            // Fees
            Fees fees = new Fees();
            fees.setTotalAmount(50000.0);
            fees.setPaidAmount(50000.0 - (sem * 1000));
            fees.setSemester(semester);
            semester.setFees(fees);

            // Result
            SemesterResult result = new SemesterResult();
            result.setResultStatus("PASS");
            result.setSemester(semester);
            semester.setResult(result);

            // Subjects
            List<String> subjectNames = getSubjectsForSemester(sem);
            List<Subject> subjects = new ArrayList<>();

            for (int i = 0; i < subjectNames.size(); i++) {

                Subject subject = Subject.builder()
                        .subjectCode("CS" + sem + "0" + (i + 1))
                        .subjectName(subjectNames.get(i))
                        .credits(4)
                        .semester(semester)
                        .build();

                Marks marks = Marks.builder()
                        .internalMarks(40.0 + i)
                        .externalMarks(35.0 + i)
                        .totalMarks((40.0 + i) + (35.0 + i))
                        .grade("A")
                        .subject(subject)
                        .build();

                subject.setMarks(marks);
                subjects.add(subject);
            }

            semester.setSubjects(subjects);

            semesterRepository.save(semester);
        }
    }

    private List<String> getSubjectsForSemester(int sem) {

        return switch (sem) {

            case 1 -> List.of(
                    "Engineering Mathematics I",
                    "Engineering Physics",
                    "Basic Electrical Engineering",
                    "Programming in C",
                    "Engineering Graphics"
            );

            case 2 -> List.of(
                    "Engineering Mathematics II",
                    "Data Structures",
                    "Digital Logic Design",
                    "Object Oriented Programming",
                    "Environmental Science"
            );

            case 3 -> List.of(
                    "Discrete Mathematics",
                    "Operating Systems",
                    "Database Management Systems",
                    "Computer Organization",
                    "Design and Analysis of Algorithms"
            );

            case 4 -> List.of(
                    "Computer Networks",
                    "Software Engineering",
                    "Theory of Computation",
                    "Microprocessors",
                    "Web Technologies"
            );

            case 5 -> List.of(
                    "Artificial Intelligence",
                    "Machine Learning",
                    "Cloud Computing",
                    "Cyber Security",
                    "Compiler Design"
            );

            default -> List.of();
        };
    }
}