package school.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.data.models.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Teacher findByUsername(String username);
}