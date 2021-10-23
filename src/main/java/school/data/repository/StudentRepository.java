package school.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.data.models.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findByUsername(String username);
}