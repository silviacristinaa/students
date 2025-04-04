package com.github.silviacristinaa.students.resources;

import com.github.silviacristinaa.students.dtos.requests.StudentRequestDto;
import com.github.silviacristinaa.students.dtos.requests.StudentStatusRequestDto;
import com.github.silviacristinaa.students.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.students.exceptions.ConflictException;
import com.github.silviacristinaa.students.exceptions.NotFoundException;
import com.github.silviacristinaa.students.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Students", description = "Student control service")
public class StudentResource {

    private static final String ID = "/{id}";

    private final StudentService studentService;

    @Operation(summary = "Get all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated students returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Page<StudentResponseDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(pageable));
    }

    @Operation(summary = "Get by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student returned successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Arguments not valid"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = ID)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<StudentResponseDto> findById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(studentService.findOneStudentById(id));
    }

    @Operation(summary = "Create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Arguments not valid"),
            @ApiResponse(responseCode = "409", description = "Conflict - CPF or email already registered"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody @Valid StudentRequestDto studentRequestDto)
            throws ConflictException {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(studentService.create(studentRequestDto).getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Patch status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Arguments not valid"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateStudentStatus(@PathVariable Long id,
                                                    @RequestBody StudentStatusRequestDto studentStatusRequestDto)
            throws NotFoundException {
        studentService.updateStudentStatus(id, studentStatusRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Arguments not valid"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - CPF or email already registered"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid StudentRequestDto studentRequestDto)
            throws NotFoundException, ConflictException {
        studentService.update(id, studentRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Arguments not valid"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
