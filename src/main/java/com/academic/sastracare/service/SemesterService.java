package com.academic.sastracare.service;

import com.academic.sastracare.entity.*;
import com.academic.sastracare.dto.SemesterResponse;
import com.academic.sastracare.dto.SubjectMarkResponse;
import com.academic.sastracare.exception.auth.ParentNotFoundException;
import com.academic.sastracare.exception.auth.UnauthorizedAccessException;
import com.academic.sastracare.exception.semester.ActiveSemesterNotFoundException;
import com.academic.sastracare.exception.semester.NoStudentLinkedException;
import com.academic.sastracare.repository.ParentRepository;
import com.academic.sastracare.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final ParentRepository parentRepository;
    private final SemesterRepository semesterRepository;
    private final HuggingFaceTtsService ttsService;
    private final MessageSource messageSource;

    // =====================================================
    // MAIN SUMMARY METHOD
    // =====================================================
    @Transactional(readOnly = true)
    public SemesterResponse getSemesterSummary(String language) {

        Locale locale = Locale.forLanguageTag(language);

        Parent parent = getLoggedParent();

        if (parent.getStudents() == null || parent.getStudents().isEmpty()) {
            throw new NoStudentLinkedException("No student linked to parent");
        }

        Student student = parent.getStudents().get(0);

        Semester semester = semesterRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() -> new ActiveSemesterNotFoundException("Active semester not found"));

        List<Subject> subjectEntities =
                semester.getSubjects() != null
                        ? semester.getSubjects()
                        : Collections.emptyList();

        List<SubjectMarkResponse> subjects = subjectEntities.stream()
                .map(subject -> {

                    Marks marks = subject.getMarks();

                    return SubjectMarkResponse.builder()
                            .subjectName(subject.getSubjectName())
                            .credits(subject.getCredits())
                            .internalMarks(marks != null ? marks.getInternalMarks() : 0.0)
                            .externalMarks(marks != null ? marks.getExternalMarks() : 0.0)
                            .totalMarks(marks != null ? marks.getTotalMarks() : 0.0)
                            .grade(marks != null ? marks.getGrade() : "N/A")
                            .build();
                })
                .toList();

        Attendance attendance = semester.getAttendance();
        Fees fees = semester.getFees();
        SemesterResult result = semester.getResult();

        double attendancePercentage = 0.0;
        if (attendance != null && attendance.getTotalDays() > 0) {
            attendancePercentage =
                    (attendance.getPresentDays() * 100.0) /
                            attendance.getTotalDays();
        }

        double pendingFees = 0.0;
        if (fees != null) {
            pendingFees = fees.getTotalAmount() - fees.getPaidAmount();
        }

        String localizedResultStatus = resolveResultStatus(result, locale);

        String message = buildMessage(
                semester,
                subjects,
                attendancePercentage,
                localizedResultStatus,
                locale
        );

        return SemesterResponse.builder()
                .studentName(student.getName())
                .semesterNumber(semester.getSemesterNumber())
                .sgpa(semester.getSgpa())
                .cgpa(student.getCgpa())
                .resultStatus(localizedResultStatus) // return localized value
                .totalDays(attendance != null ? attendance.getTotalDays() : 0)
                .presentDays(attendance != null ? attendance.getPresentDays() : 0)
                .attendancePercentage(attendancePercentage)
                .totalFees(fees != null ? fees.getTotalAmount() : 0.0)
                .paidFees(fees != null ? fees.getPaidAmount() : 0.0)
                .pendingFees(pendingFees)
                .subjects(subjects)
                .messageText(message)
                .build();
    }

    // =====================================================
    // AUDIO METHOD
    // =====================================================
    @Transactional(readOnly = true)
    public byte[] getSemesterSummaryAudio(String language) {

        SemesterResponse summary = getSemesterSummary(language);

        return ttsService.convertTextToSpeech(
                summary.getMessageText(),
                language
        );
    }

    // =====================================================
    // SECURITY
    // =====================================================
    private Parent getLoggedParent() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Authentication required");
        }

        String parentId = authentication.getName();

        return parentRepository.findById(parentId)
                .orElseThrow(() -> new ParentNotFoundException("Parent not found"));
    }

    // =====================================================
    // RESULT LOCALIZATION (FIXED PROPERLY)
    // =====================================================
    private String resolveResultStatus(SemesterResult result, Locale locale) {

        if (result == null || result.getResultStatus() == null) {
            return messageSource.getMessage(
                    "result.notDeclared",
                    null,
                    locale
            );
        }

        try {
            return messageSource.getMessage(
                    "result." + result.getResultStatus(),
                    null,
                    locale
            );
        } catch (Exception ex) {
            return result.getResultStatus();
        }
    }

    // =====================================================
    // MESSAGE BUILDER
    // =====================================================
    private String buildMessage(Semester semester,
                                List<SubjectMarkResponse> subjects,
                                double attendancePercentage,
                                String localizedResultStatus,
                                Locale locale) {

        String mainMessage = messageSource.getMessage(
                "semester.summary",
                new Object[]{
                        semester.getSemesterNumber(),
                        String.format("%.2f", attendancePercentage),
                        semester.getSgpa(),
                        localizedResultStatus
                },
                locale
        );

        StringBuilder sb = new StringBuilder(mainMessage);

        for (SubjectMarkResponse s : subjects) {

            String subjectLine = messageSource.getMessage(
                    "semester.subject",
                    new Object[]{
                            s.getSubjectName(),
                            s.getTotalMarks(),
                            s.getGrade()
                    },
                    locale
            );

            sb.append(" ").append(subjectLine);
        }

        return sb.toString();
    }
}