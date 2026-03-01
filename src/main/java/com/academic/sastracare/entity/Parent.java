package com.academic.sastracare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "parents", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String mobile;

    private boolean active = true;

    private boolean mobileVerified = true;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Student> students;
}