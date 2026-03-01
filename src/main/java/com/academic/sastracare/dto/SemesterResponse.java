package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SemesterResponse {

    private String studentName;
    private Integer semesterNumber;

    private Double sgpa;
    private Double cgpa;
    private String resultStatus;

    private Integer totalDays;
    private Integer presentDays;
    private Double attendancePercentage;

    private Double totalFees;
    private Double paidFees;
    private Double pendingFees;

    private List<SubjectMarkResponse> subjects;

    private String messageText;
}