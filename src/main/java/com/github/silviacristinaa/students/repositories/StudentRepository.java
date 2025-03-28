package com.github.silviacristinaa.students.repositories;

import com.github.silviacristinaa.students.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByCpf(String cpf);

    Optional<Student> findByEmail(String email);
}
