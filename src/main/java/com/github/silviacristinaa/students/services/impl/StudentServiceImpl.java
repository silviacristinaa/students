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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter @Setter
public class StudentServiceImpl implements StudentService {

    private static final String CPF_ALREADY_REGISTERED_IN_THE_SYSTEM = "CPF already registered in the system";
    private static final String EMAIL_ALREADY_REGISTERED_IN_THE_SYSTEM = "Email already registered in the system";
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
        checkCpfExists(studentRequestDto.getCpf(), null);
        checkEmailExists(studentRequestDto.getEmail(), null);

        Student student = modelMapper.map(studentRequestDto, Student.class);
        String registration = generateRegistration();
        student.setRegistration(registration);

        return studentRepository.save(student);
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
        Student student = findById(id);

        checkCpfExists(studentRequestDto.getCpf(), id);
        checkEmailExists(studentRequestDto.getEmail(), id);

        student.setName(studentRequestDto.getName());
        student.setCpf(studentRequestDto.getCpf());
        student.setEmail(studentRequestDto.getEmail());
        student.setCourse(studentRequestDto.getCourse());
        student.setActive(studentRequestDto.isActive());

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

    private void checkCpfExists(String cpf, Long id) throws ConflictException {
        Optional<Student> student = studentRepository.findByCpf(cpf);
        if(student.isPresent() && (id == null || !id.equals(student.get().getId()))) {
            throw new ConflictException(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM);
        }
    }

    private void checkEmailExists(String email, Long id) throws ConflictException {
        Optional<Student> student = studentRepository.findByEmail(email);
        if(student.isPresent() && (id == null || !id.equals(student.get().getId()))) {
            throw new ConflictException(EMAIL_ALREADY_REGISTERED_IN_THE_SYSTEM);
        }
    }

    private String generateRegistration() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
