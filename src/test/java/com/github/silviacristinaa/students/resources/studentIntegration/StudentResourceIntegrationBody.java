package com.github.silviacristinaa.students.resources.studentIntegration;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.enums.CourseEnum;

public class StudentResourceIntegrationBody {

    public static StudentRequestDto studentException() {
        return new StudentRequestDto(null, "12345678909", "test@gmail.com", CourseEnum.LAW, true);
    }

    public static StudentRequestDto studentSuccess() {
        return new StudentRequestDto("Test", "12345678909", "test@gmail.com", CourseEnum.LAW, true);
    }

    public static StudentRequestDto studentWithEmailConflict() {
        StudentRequestDto student = studentSuccess();
        student.setCpf("34362506276");
        return student;
    }

    public static StudentStatusRequestDto updateStudentStatus() {
        return new StudentStatusRequestDto(false);
    }
}
