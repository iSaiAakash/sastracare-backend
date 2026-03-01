package com.academic.sastracare.service;

import com.academic.sastracare.dto.*;
import com.academic.sastracare.entity.*;
import com.academic.sastracare.exception.auth.ParentNotFoundException;
import com.academic.sastracare.exception.auth.UnauthorizedAccessException;
import com.academic.sastracare.exception.semester.DataNotAvailableException;
import com.academic.sastracare.repository.ParentRepository;
import com.academic.sastracare.repository.SemesterRepository;
import com.academic.sastracare.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StudentQueryService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final MessageSource messageSource;

    // ==========================================
    // AUTH UTIL
    // ==========================================
    private String getLoggedParentId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Unauthorized access");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof String parentId)) {
            throw new AccessDeniedException("Invalid authentication context");
        }

        return parentId;
    }

    private Student getStudentOwnedByParent(Long studentId) {

        String parentId = getLoggedParentId();

        return studentRepository
                .findByIdAndParentId(studentId, parentId)
                .orElseThrow(() ->
                        new UnauthorizedAccessException("Unauthorized access"));
    }

    private Semester getSemester(Student student, Integer semesterNumber) {

        return semesterRepository
                .findByStudentAndSemesterNumber(student, semesterNumber)
                .orElseThrow(() ->
                        new DataNotAvailableException("Semester not found"));
    }

    // ==========================================
    // AVAILABLE SEMESTERS
    // ==========================================
    public List<Integer> getAvailableSemesters(Long studentId) {

        Student student = getStudentOwnedByParent(studentId);

        return semesterRepository
                .findByStudent(student)
                .stream()
                .map(Semester::getSemesterNumber)
                .sorted()
                .toList();
    }

    // ==========================================
    // ATTENDANCE
    // ==========================================
    public AttendanceResponse getAttendance(
            Long studentId,
            Integer semesterNumber,
            String language) {

        Locale locale = Locale.forLanguageTag(language);

        Student student = getStudentOwnedByParent(studentId);
        Semester semester = getSemester(student, semesterNumber);

        Attendance attendance = semester.getAttendance();

        if (attendance == null) {
            throw new DataNotAvailableException(
                    messageSource.getMessage("attendance.notAvailable", null, locale));
        }

        int totalDays = attendance.getTotalDays();
        int presentDays = attendance.getPresentDays();

        double percentage = totalDays > 0
                ? (presentDays * 100.0) / totalDays
                : 0.0;

        return AttendanceResponse.builder()
                .semesterNumber(semesterNumber)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    // ==========================================
    // FEES
    // ==========================================
    public FeesResponse getFees(
            Long studentId,
            Integer semesterNumber,
            String language) {

        Locale locale = Locale.forLanguageTag(language);

        Student student = getStudentOwnedByParent(studentId);
        Semester semester = getSemester(student, semesterNumber);

        Fees fees = semester.getFees();

        if (fees == null) {
            throw new DataNotAvailableException(
                    messageSource.getMessage("fees.notAvailable", null, locale));
        }

        double pending = fees.getTotalAmount() - fees.getPaidAmount();

        return FeesResponse.builder()
                .semesterNumber(semesterNumber)
                .totalAmount(fees.getTotalAmount())
                .paidAmount(fees.getPaidAmount())
                .pendingAmount(pending)
                .build();
    }

    // ==========================================
    // SGPA
    // ==========================================
    public SgpaResponse getSgpa(
            Long studentId,
            Integer semesterNumber,
            String language) {

        Student student = getStudentOwnedByParent(studentId);
        Semester semester = getSemester(student, semesterNumber);

        return SgpaResponse.builder()
                .semesterNumber(semesterNumber)
                .sgpa(semester.getSgpa())
                .build();
    }

    // ==========================================
    // CGPA (CUMULATIVE)
    // ==========================================
    public CgpaResponse getCgpa(Long studentId, String language) {

        Student student = getStudentOwnedByParent(studentId);

        return CgpaResponse.builder()
                .cgpa(student.getCgpa())
                .build();
    }

    // ==========================================
    // RESULT
    // ==========================================
    @Transactional(readOnly = true)
    public ExamResultResponse getResult(
            Long studentId,
            Integer semesterNumber,
            String language) {

        Student student = getStudentOwnedByParent(studentId);
        Semester semester = getSemester(student, semesterNumber);

        var subjects = semester.getSubjects();

        if (subjects == null || subjects.isEmpty()) {
            throw new DataNotAvailableException("Result not available");
        }

        int totalSubjects = subjects.size();
        int passed = 0;
        int arrears = 0;

        List<SubjectResultResponse> subjectDtos = new ArrayList<>();

        for (Subject subject : subjects) {

            Marks marks = subject.getMarks();

            if (marks == null) {
                throw new DataNotAvailableException("Marks not available for subject");
            }

            String grade = marks.getGrade();
            boolean isFail = grade.equalsIgnoreCase("F");

            if (isFail) {
                arrears++;
            } else {
                passed++;
            }

            subjectDtos.add(
                    SubjectResultResponse.builder()
                            .subjectName(subject.getSubjectName())
                            .totalMarks(marks.getTotalMarks())
                            .grade(grade)
                            .status(isFail ? "FAIL" : "PASS")
                            .build()
            );
        }

        return ExamResultResponse.builder()
                .semesterNumber(semesterNumber)
                .totalSubjects(totalSubjects)
                .passed(passed)
                .arrears(arrears)
                .subjects(subjectDtos)
                .build();
    }

    // ==========================================
    // CHILDREN
    // ==========================================
    public List<StudentSummaryResponse> getChildrenOfParent(String parentId) {

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));

        return studentRepository.findByParent(parent)
                .stream()
                .map(student -> new StudentSummaryResponse(
                        student.getId(),
                        student.getName(),
                        student.getRegisterNumber(),
                        student.getProgram()))
                .toList();
    }
}