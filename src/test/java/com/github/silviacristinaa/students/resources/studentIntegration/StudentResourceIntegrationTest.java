package com.github.silviacristinaa.students.resources.studentIntegration;

import com.github.silviacristinaa.students.entities.Student;
import com.github.silviacristinaa.students.enums.CourseEnum;
import com.github.silviacristinaa.students.repositories.StudentRepository;
import com.github.silviacristinaa.students.resources.integrations.IntegrationTests;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentResourceIntegrationTest extends IntegrationTests {

    private static final String CONFLICT = "Conflict";
    private static final String NOT_FOUND_MSG = "Not found";
    private static final String STUDENT_NOT_FOUND = "Student %s not found";

    private static final String NAME = "Test";
    private static final String CPF = "12345678909";
    private static final String EMAIL = "test@gmail.com";

    private String studentId;
    private Long studentUpdateId;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Order(1)
    public void whenTryCreateStudentWithInvalidFieldsReturnBadRequestException() throws Exception {
        mvc.perform(post("/students").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Arguments not valid")));
    }

    @Test
    @Order(2)
    public void whenCreateStudentReturnCreated() throws Exception {
        mvc.perform(post("/students").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentSuccess())))
                .andExpect(status().isCreated())
                .andDo(i -> studentId = getIdByLocation(i.getResponse().getHeader("Location")));
    }

    @Test
    @Order(3)
    public void whenTryCreateStudentWithAlreadyExistingCpfReturnConflictException() throws Exception {
        mvc.perform(post("/students").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentSuccess())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is(CONFLICT)))
                .andExpect(jsonPath("errors.[0]", is("CPF already registered in the system")));
    }

    @Test
    @Order(4)
    public void whenTryCreateStudentWithAlreadyExistingEmailReturnConflictException() throws Exception {
        mvc.perform(post("/students").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentWithEmailConflict())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is(CONFLICT)))
                .andExpect(jsonPath("errors.[0]", is("Email already registered in the system")));
    }

    @Test
    @Order(5)
    public void whenFindAllReturnSuccess() throws Exception {
        mvc.perform(get("/students").headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].name", is(NAME)))
                .andExpect(jsonPath("content[0].cpf", is(CPF)))
                .andExpect(jsonPath("content[0].email", is(EMAIL)))
                .andExpect(jsonPath("content[0].registration", notNullValue()))
                .andExpect(jsonPath("content[0].course", is("LAW")))
                .andExpect(jsonPath("content[0].active", is(true)));
    }

    @Test
    @Order(6)
    public void whenTryFindByIdWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(get("/students/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is(String.format(STUDENT_NOT_FOUND, 999))));
    }

    @Test
    @Order(7)
    public void whenFindByIdWithCorrectIdReturnSuccess() throws Exception {
        mvc.perform(get("/students/{id}", studentId).headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(NAME)))
                .andExpect(jsonPath("cpf", is(CPF)))
                .andExpect(jsonPath("email", is(EMAIL)))
                .andExpect(jsonPath("registration", notNullValue()))
                .andExpect(jsonPath("course", is("LAW")))
                .andExpect(jsonPath("active", is(true)));
    }

    @Test
    @Order(8)
    public void whenTryUpdateStudentStatusWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(patch("/students/{id}", 999).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.updateStudentStatus())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is(String.format(STUDENT_NOT_FOUND, 999))));
    }

    @Test
    @Order(9)
    public void whenUpdateStudentStatusWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(patch("/students/{id}", studentId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.updateStudentStatus())))
                .andExpect(status().isNoContent());

        Optional<Student> student = studentRepository.findById(Long.valueOf(studentId));

        assertTrue(student.isPresent());
        assertEquals(student.get().getName(), NAME);
        assertEquals(student.get().getCpf(), CPF);
        assertEquals(student.get().getEmail(), EMAIL);
        assertNotNull(student.get().getRegistration());
        assertEquals(student.get().getCourse(), CourseEnum.LAW);
        assertFalse(student.get().isActive());
    }

    @Test
    @Order(10)
    public void whenTryUpdateStudentWithInvalidFieldsReturnBadRequestException() throws Exception {
        mvc.perform(put("/students/{id}", studentId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Arguments not valid"));
    }

    @Test
    @Order(11)
    public void whenTryUpdateStudentWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(put("/students/{id}", 999).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentSuccess())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is((String.format(STUDENT_NOT_FOUND, 999)))));
    }

    @Test
    @Order(12)
    public void whenTryUpdateStudentWithAlreadyExistingCpfReturnConflictException() throws Exception {
        Student student = studentRepository.save(new Student(null, NAME, "34362506276",
                "test1@gmail.com", "20259212", CourseEnum.LAW, true));

        studentUpdateId = student.getId();

        mvc.perform(put("/students/{id}", student.getId()).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentSuccess())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is(CONFLICT)))
                .andExpect(jsonPath("errors.[0]", is("CPF already registered in the system")));
    }

    @Test
    @Order(13)
    public void whenTryUpdateStudentWithAlreadyExistingEmailReturnConflictException() throws Exception {
        mvc.perform(put("/students/{id}", studentUpdateId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentWithEmailConflict())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is(CONFLICT)))
                .andExpect(jsonPath("errors.[0]", is("Email already registered in the system")));
    }

    @Test
    @Order(14)
    public void whenUpdateStudentWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(put("/students/{id}", studentId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                StudentResourceIntegrationBody.studentSuccess())))
                .andExpect(status().isNoContent());

        Optional<Student> student = studentRepository.findById(Long.valueOf(studentId));

        assertTrue(student.isPresent());
        assertEquals(student.get().getName(), NAME);
        assertEquals(student.get().getCpf(), CPF);
        assertEquals(student.get().getEmail(), EMAIL);
        assertNotNull(student.get().getRegistration());
        assertEquals(student.get().getCourse(), CourseEnum.LAW);
        assertTrue(student.get().isActive());
    }

    @Test
    @Order(15)
    public void whenTryDeleteStudentWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(delete("/students/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is((String.format(STUDENT_NOT_FOUND, 999)))));
    }

    @Test
    @Order(16)
    public void whenDeleteStudentWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(delete("/students/{id}", studentId).headers(mockHttpHeaders()))
                .andExpect(status().isNoContent());

        Optional<Student> student = studentRepository.findById(Long.valueOf(studentId));
        assertFalse(student.isPresent());
    }
}
