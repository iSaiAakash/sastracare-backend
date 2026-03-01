package com.academic.sastracare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeesResponse {

    private Integer semesterNumber;
    private Double totalAmount;
    private Double paidAmount;
    private Double pendingAmount;
}