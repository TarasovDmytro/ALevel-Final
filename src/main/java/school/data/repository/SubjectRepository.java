package school.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.data.models.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

}