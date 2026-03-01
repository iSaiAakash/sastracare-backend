package com.academic.sastracare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StudentSummaryResponse {

    private Long id;
    private String name;
    private String registerNumber;
    private String program;

}