package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamResultResponse {

    private Integer semesterNumber;

    private Integer totalSubjects;
    private Integer passed;
    private Integer arrears;

    private List<SubjectResultResponse> subjects;
}