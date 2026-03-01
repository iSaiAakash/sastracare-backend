package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SgpaResponse {

    private Integer semesterNumber;
    private Double sgpa;
}