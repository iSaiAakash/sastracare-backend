package com.academic.sastracare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "marks")
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double internalMarks;
    private Double externalMarks;
    private Double totalMarks;
    private String grade; // O, A+, A, B, etc

    @OneToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}