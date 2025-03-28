package com.github.silviacristinaa.students.dtos.requests;

import com.github.silviacristinaa.students.enums.CourseEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class StudentRequestDto {

    @NotBlank
    private String name;
    @NotBlank
    @CPF(message = "The CPF must be valid")
    private String cpf;
    @NotBlank
    @Email(message = "The email must be valid")
    private String email;
    @NotNull
    private CourseEnum course;
    private boolean active;
}
