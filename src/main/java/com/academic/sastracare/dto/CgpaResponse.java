package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CgpaResponse {

    private Double cgpa;
}