    package com.academic.sastracare.config;

    import com.academic.sastracare.entity.*;
    import com.academic.sastracare.repository.ParentRepository;
    import com.academic.sastracare.repository.SemesterRepository;
    import com.academic.sastracare.repository.StudentRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.stereotype.Component;

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
                System.out.println("Sample parent already exists. Skipping initialization.");
                return;
            }

            if (studentRepository.existsByRegisterNumber("226027001")) {
                System.out.println("Sample student already exists. Skipping initialization.");
                return;
            }

            // =========================
            // 1️⃣ PARENT (FIXED ID)
            // =========================
            Parent parent = new Parent();
            parent.setMobile("9876543210");
            parent.setActive(true);
            parent.setMobileVerified(true);
            parent.setName("Ramesh Kumar");

            parentRepository.save(parent);

            // =========================
            // 2️⃣ STUDENT
            // =========================
            Student student = new Student();
            student.setName("Arjun");
            student.setCgpa(8.5);
            student.setRegisterNumber("226027001");
            student.setProgram("III Year CSE");
            student.setParent(parent);

            studentRepository.save(student);

            // =========================
            // 3️⃣ SEMESTER (ACTIVE)
            // =========================
            Semester semester = new Semester();
            semester.setSemesterNumber(3);
            semester.setSgpa(8.7);
            semester.setActive(true);   // 🔥 REQUIRED for your service
            semester.setStudent(student);

            // =========================
            // 4️⃣ ATTENDANCE
            // =========================
            Attendance attendance = new Attendance();
            attendance.setTotalDays(90);
            attendance.setPresentDays(82);
            attendance.setSemester(semester);
            semester.setAttendance(attendance);

            // =========================
            // 5️⃣ FEES
            // =========================
            Fees fees = new Fees();
            fees.setTotalAmount(50000.0);
            fees.setPaidAmount(45000.0);
            fees.setSemester(semester);
            semester.setFees(fees);

            // =========================
            // 6️⃣ RESULT
            // =========================
            SemesterResult result = new SemesterResult();
            result.setResultStatus("PASS");
            result.setSemester(semester);
            semester.setResult(result);

            // =========================
            // 7️⃣ SUBJECTS + MARKS
            // =========================
            Subject maths = Subject.builder()
                    .subjectCode("CS301")
                    .subjectName("Data Structures")
                    .credits(4)
                    .semester(semester)
                    .build();

            Marks mathsMarks = Marks.builder()
                    .internalMarks(45.0)
                    .externalMarks(40.0)
                    .totalMarks(85.0)
                    .grade("A+")
                    .subject(maths)
                    .build();

            maths.setMarks(mathsMarks);

            Subject os = Subject.builder()
                    .subjectCode("CS302")
                    .subjectName("Operating Systems")
                    .credits(4)
                    .semester(semester)
                    .build();

            Marks osMarks = Marks.builder()
                    .internalMarks(42.0)
                    .externalMarks(38.0)
                    .totalMarks(80.0)
                    .grade("A")
                    .subject(os)
                    .build();

            os.setMarks(osMarks);

            semester.setSubjects(List.of(maths, os));

            // =========================
            // SAVE EVERYTHING
            // =========================
            semesterRepository.save(semester);

            System.out.println("✅ Clean sample data inserted successfully");
        }
    }