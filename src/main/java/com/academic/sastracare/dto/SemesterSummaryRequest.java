package com.academic.sastracare.dto;

import lombok.Getter;

@Getter
public class SemesterSummaryRequest {

    private String mobile;
    private String language; // "ta" or "en"
}