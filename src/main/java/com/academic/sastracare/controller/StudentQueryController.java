package com.academic.sastracare.controller;

import com.academic.sastracare.dto.*;
import com.academic.sastracare.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-children")
@RequiredArgsConstructor
public class StudentQueryController {

    private final StudentQueryService studentQueryService;

    @GetMapping
    public List<StudentSummaryResponse> getMyChildren(
            Authentication authentication) {

        return studentQueryService.getChildrenOfParent(authentication.getName());
    }

    @GetMapping("/{studentId}/semesters")
    public List<Integer> getAvailableSemesters(
            @PathVariable Long studentId) {

        return studentQueryService.getAvailableSemesters(studentId);
    }

    @GetMapping("/{studentId}/attendance")
    public AttendanceResponse getAttendance(
            @PathVariable Long studentId,
            @RequestParam Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return studentQueryService.getAttendance(studentId, semester, language);
    }

    @GetMapping("/{studentId}/fees")
    public FeesResponse getFees(
            @PathVariable Long studentId,
            @RequestParam Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return studentQueryService.getFees(studentId, semester, language);
    }

    @GetMapping("/{studentId}/sgpa")
    public SgpaResponse getSgpa(
            @PathVariable Long studentId,
            @RequestParam Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return studentQueryService.getSgpa(studentId, semester, language);
    }

    @GetMapping("/{studentId}/cgpa")
    public CgpaResponse getCgpa(
            @PathVariable Long studentId,
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return studentQueryService.getCgpa(studentId, language);
    }

    @GetMapping("/{studentId}/result")
    public ExamResultResponse getResult(
            @PathVariable Long studentId,
            @RequestParam Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return studentQueryService.getResult(studentId, semester, language);
    }
}