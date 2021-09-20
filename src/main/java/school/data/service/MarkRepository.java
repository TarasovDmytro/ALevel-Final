package school.data.service;

import school.data.entity.Mark;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface MarkRepository extends JpaRepository<Mark, Integer> {

}