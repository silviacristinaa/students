package com.github.silviacristinaa.students.dtos.responses;

import com.github.silviacristinaa.students.enums.CourseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class StudentResponseDto {

    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String registration;
    private CourseEnum course;
    private boolean active;
}
