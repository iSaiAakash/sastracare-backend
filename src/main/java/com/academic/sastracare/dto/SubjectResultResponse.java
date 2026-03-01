package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResultResponse {

    private String subjectName;
    private Double totalMarks;
    private String grade;
    private String status; // PASS / FAIL
}