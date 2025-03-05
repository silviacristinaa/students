package com.github.silviacristinaa.students.services.impl;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.students.entities.Student;
import com.github.silviacristinaa.students.enums.CourseEnum;
import com.github.silviacristinaa.students.exceptions.ConflictException;
import com.github.silviacristinaa.students.exceptions.NotFoundException;
import com.github.silviacristinaa.students.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class StudentServiceImplTest {

    private static final String CPF_ALREADY_REGISTERED_IN_THE_SYSTEM = "CPF already registered in the system";
    private static final String EMAIL_ALREADY_REGISTERED_IN_THE_SYSTEM = "Email already registered in the system";
    private static final String STUDENT_NOT_FOUND = "Student %s not found";

    private static final long ID = 1L;
    private static final String NAME = "Test";
    private static final String CPF = "123.456.789-09";
    private static final String EMAIL = "test@gmail.com";
    private static final String REGISTRATION = "20259212";
    private static final int INDEX = 0;

    private StudentRequestDto studentRequestDto;
    private StudentStatusRequestDto studentStatusRequestDto;
    private StudentResponseDto studentResponseDto;

    private Student student;

    @InjectMocks
    private StudentServiceImpl studentServiceImpl;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        studentRequestDto = new StudentRequestDto(NAME, CPF, EMAIL, CourseEnum.LAW, false);

        studentStatusRequestDto = new StudentStatusRequestDto(true);

        studentResponseDto = new StudentResponseDto(ID, NAME, CPF, EMAIL, REGISTRATION, CourseEnum.LAW, true);

        student = new Student(ID, NAME, CPF, EMAIL, REGISTRATION, CourseEnum.LAW, true);
    }

    @Test
    void whenFindAllReturnStudentResponseDtoPage() {
        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(studentResponseDto);

        Page<StudentResponseDto> response = studentServiceImpl.findAll(Pageable.ofSize(1));

        assertNotNull(response);
        assertEquals(1, response.getSize());
        assertEquals(StudentResponseDto.class, response.getContent().get(INDEX).getClass());

        assertEquals(ID, response.getContent().get(INDEX).getId());
        assertEquals(NAME, response.getContent().get(INDEX).getName());
        assertEquals(CPF, response.getContent().get(INDEX).getCpf());
        assertEquals(EMAIL, response.getContent().get(INDEX).getEmail());
        assertEquals(REGISTRATION, response.getContent().get(INDEX).getRegistration());
        assertEquals(CourseEnum.LAW, response.getContent().get(INDEX).getCourse());
        assertTrue(response.getContent().get(INDEX).isActive());
    }

    @Test
    void whenFindByIdReturnOneStudentResponseDto() throws NotFoundException {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(studentResponseDto);

        StudentResponseDto response = studentServiceImpl.findOneStudentById(ID);

        assertNotNull(response);

        assertEquals(StudentResponseDto.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(CPF, response.getCpf());
        assertEquals(EMAIL, response.getEmail());
        assertEquals(REGISTRATION, response.getRegistration());
        assertEquals(CourseEnum.LAW, response.getCourse());
        assertTrue(response.isActive());
    }

    @Test
    void whenTryFindByIdReturnNotFoundException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> studentServiceImpl.findOneStudentById(ID));

        assertEquals(String.format(STUDENT_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenCreateReturnSuccess() throws ConflictException {
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(student);
        when(studentRepository.save(Mockito.any())).thenReturn(student);

        Student response = studentServiceImpl.create(studentRequestDto);

        assertNotNull(response);
        assertNotNull(response.getRegistration());
        assertEquals(Student.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(NAME, response.getName());
        assertEquals(CPF, response.getCpf());
        assertEquals(EMAIL, response.getEmail());
        assertEquals(CourseEnum.LAW, response.getCourse());
        assertTrue(response.isActive());

        verify(studentRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryCreateReturnCpfConflictException() {
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.of(student));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> studentServiceImpl.create(studentRequestDto));

        assertEquals(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM, exception.getMessage());
    }

    @Test
    void whenTryCreateReturnEmailConflictException() {
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(student));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> studentServiceImpl.create(studentRequestDto));

        assertEquals(EMAIL_ALREADY_REGISTERED_IN_THE_SYSTEM, exception.getMessage());
    }

    @Test
    void whenUpdateStudentStatusReturnSuccess() throws NotFoundException {
        when(studentRepository.findById(Mockito.any())).thenReturn(Optional.of(student));

        studentServiceImpl.updateStudentStatus(ID, studentStatusRequestDto);

        verify(studentRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateStudentStatusReturnNotFoundException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> studentServiceImpl.updateStudentStatus(ID, studentStatusRequestDto));

        assertEquals(String.format(STUDENT_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenUpdateReturnSuccess() throws NotFoundException, ConflictException {
        when(studentRepository.findById(Mockito.any())).thenReturn(Optional.of(student));
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(student);

        studentServiceImpl.update(ID, studentRequestDto);

        verify(studentRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateReturnNotFoundException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> studentServiceImpl.update(ID, studentRequestDto));

        assertEquals(String.format(STUDENT_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenTryUpdateReturnCpfConflictException() {
        when(studentRepository.findById(Mockito.any())).thenReturn(Optional.of(student));
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.of(student));

        student.setId(2L);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> studentServiceImpl.update(ID, studentRequestDto));

        assertEquals(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM, exception.getMessage());
    }

    @Test
    void whenTryUpdateReturnEmailConflictException() {
        when(studentRepository.findById(Mockito.any())).thenReturn(Optional.of(student));
        when(studentRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(student));

        student.setId(2L);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> studentServiceImpl.update(ID, studentRequestDto));

        assertEquals(EMAIL_ALREADY_REGISTERED_IN_THE_SYSTEM, exception.getMessage());
    }

    @Test
    void whenDeleteReturnSuccess() throws NotFoundException {
        when(studentRepository.findById(Mockito.any())).thenReturn(Optional.of(student));

        studentServiceImpl.delete(ID);

        verify(studentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenTryDeleteReturnNotFoundException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> studentServiceImpl.delete(ID));

        assertEquals(String.format(STUDENT_NOT_FOUND, ID), exception.getMessage());
    }
}
