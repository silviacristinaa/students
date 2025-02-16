package com.github.silviacristinaa.students.services.impl;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.students.entities.Student;
import com.github.silviacristinaa.students.exceptions.ConflictException;
import com.github.silviacristinaa.students.exceptions.NotFoundException;
import com.github.silviacristinaa.students.repositories.StudentRepository;
import com.github.silviacristinaa.students.services.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private static final String CPF_ALREADY_REGISTERED_IN_THE_SYSTEM = "Cpf already registered in the system";
    private static final String STUDENT_NOT_FOUND = "Student %s not found";

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<StudentResponseDto> findAll(Pageable pageable) {
        List<StudentResponseDto> response =
                studentRepository.findAll().stream().map(student -> modelMapper.map(student, StudentResponseDto.class))
                        .collect(Collectors.toList());

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), response.size());

        Page<StudentResponseDto> page = new PageImpl<>(response.subList(start, end), pageable, response.size());
        return page;
    }

    @Override
    public StudentResponseDto findOneStudentById(Long id) throws NotFoundException {
        Student student = findById(id);
        return modelMapper.map(student, StudentResponseDto.class);
    }

    @Override
    @Transactional
    public Student create(StudentRequestDto studentRequestDto) throws ConflictException {
        findByCpf(studentRequestDto);
        return studentRepository.save(modelMapper.map(studentRequestDto, Student.class));
    }

    @Override
    @Transactional
    public void updateStudentStatus(Long id, StudentStatusRequestDto studentStatusRequestDto) throws NotFoundException {
        Student student = findById(id);

        student.setActive(studentStatusRequestDto.isActive());
        student.setId(id);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void update(Long id, StudentRequestDto studentRequestDto) throws NotFoundException, ConflictException {
        findById(id);
        findByCpf(studentRequestDto, id);

        Student student = modelMapper.map(studentRequestDto, Student.class);
        student.setId(id);

        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void delete(Long id) throws NotFoundException {
        findById(id);
        studentRepository.deleteById(id);
    }

    private Student findById(Long id) throws NotFoundException {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(STUDENT_NOT_FOUND, id)));
    }

    private void findByCpf(StudentRequestDto studentRequestDto) throws ConflictException {
        Optional<Student> student = studentRepository.findByCpf(studentRequestDto.getCpf());
        if(student.isPresent()) {
            throw new ConflictException(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM);
        }
    }

    private void findByCpf(StudentRequestDto studentRequestDto, Long id) throws ConflictException {
        Optional<Student> student = studentRepository.findByCpf(studentRequestDto.getCpf());
        if(student.isPresent() && !id.equals(student.get().getId())) {
            throw new ConflictException(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM);
        }
    }
}
