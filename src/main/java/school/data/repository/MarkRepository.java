package school.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.data.models.entity.Mark;
import school.data.models.entity.Student;
import school.data.models.entity.Subject;

import java.time.LocalDate;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Integer> {
    List<Mark> findByStudent(Student student);
    List<Mark> findBySubject(Subject subject);
    List<Mark> findByStudentAndSubject(Student student, Subject subject);
    List<Mark> findByDateAfter(LocalDate fromDate);
    List<Mark> findByDateBefore(LocalDate toDate);
    List<Mark> findByDateBetween(LocalDate fromDate, LocalDate toDate);
    List<Mark> findByStudentAndDateBefore(Student student, LocalDate toDate);
    List<Mark> findByStudentAndDateAfter(Student student, LocalDate fromDate);
    List<Mark> findByStudentAndDateBetween(Student student, LocalDate fromDate, LocalDate toDate);
    List<Mark> findBySubjectAndDateBefore(Subject subject, LocalDate toDate);
    List<Mark> findBySubjectAndDateAfter(Subject subject, LocalDate fromDate);
    List<Mark> findBySubjectAndDateBetween(Subject subject, LocalDate fromDate, LocalDate toDate);
    List<Mark> findByStudentAndSubjectAndDateBefore(Student student, Subject subject, LocalDate toDate);
    List<Mark> findByStudentAndSubjectAndDateAfter(Student student, Subject subject, LocalDate fromDate);
    List<Mark> findByStudentAndSubjectAndDateBetween(Student student, Subject subject, LocalDate fromDate, LocalDate toDate);
}