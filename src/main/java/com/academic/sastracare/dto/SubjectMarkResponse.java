package com.academic.sastracare.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectMarkResponse {

    private String subjectName;
    private Integer credits;
    private Double internalMarks;
    private Double externalMarks;
    private Double totalMarks;
    private String grade;
}