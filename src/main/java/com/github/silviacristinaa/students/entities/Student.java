package com.github.silviacristinaa.students.entities;

import com.github.silviacristinaa.students.enums.CourseEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    @Email(message = "The email must be valid")
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String registration;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseEnum course;
    @Column(nullable = false)
    private boolean active;
}
