package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceResponse {

    private Integer semesterNumber;
    private int totalDays;
    private int presentDays;
    private Double percentage;
}