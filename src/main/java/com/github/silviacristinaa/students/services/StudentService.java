package com.github.silviacristinaa.students.services;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.students.entities.Student;
import com.github.silviacristinaa.students.exceptions.ConflictException;
import com.github.silviacristinaa.students.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    Page<StudentResponseDto> findAll(Pageable pageable);

    StudentResponseDto findOneStudentById(Long id) throws NotFoundException;

    Student create(StudentRequestDto studentRequestDto) throws ConflictException;

    void updateStudentStatus(Long id, StudentStatusRequestDto studentStatusRequestDto) throws NotFoundException;

    void update(Long id, StudentRequestDto studentRequestDto) throws NotFoundException, ConflictException;

    void delete(Long id) throws NotFoundException;
}
