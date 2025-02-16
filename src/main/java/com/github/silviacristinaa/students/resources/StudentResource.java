package com.github.silviacristinaa.students.resources;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.students.exceptions.ConflictException;
import com.github.silviacristinaa.students.exceptions.NotFoundException;
import com.github.silviacristinaa.students.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/students")
@RequiredArgsConstructor
public class StudentResource {

    private static final String ID = "/{id}";

    private final StudentService studentService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Page<StudentResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(pageable));
    }

    @GetMapping(value = ID)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<StudentResponseDto> findById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(studentService.findOneStudentById(id));
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody @Valid StudentRequestDto studentRequestDto) throws ConflictException {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(studentService.create(studentRequestDto).getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateStudentStatus(@PathVariable Long id,
                                                     @RequestBody StudentStatusRequestDto studentStatusRequestDto) throws NotFoundException {
        studentService.updateStudentStatus(id, studentStatusRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid StudentRequestDto studentRequestDto) throws NotFoundException, ConflictException {
        studentService.update(id, studentRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
