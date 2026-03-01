package com.academic.sastracare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int totalDays;

    @Column(nullable = false)
    private int presentDays;

    @OneToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
}