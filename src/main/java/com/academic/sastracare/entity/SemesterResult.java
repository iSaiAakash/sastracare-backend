package com.academic.sastracare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "semester_results")
public class SemesterResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resultStatus; // PASS / FAIL

    @OneToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
}