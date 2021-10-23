package school.data.service;

import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import school.data.models.entity.Mark;
import school.data.models.entity.Student;
import school.data.models.entity.Subject;
import school.data.repository.MarkRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarkService extends CrudService<Mark, Integer> {

    private final MarkRepository repository;

    public MarkService(MarkRepository repository) {
        this.repository = repository;
    }


    @Override
    protected MarkRepository getRepository() {
        return repository;
    }

    public List<Mark> findAll() {
        List<Mark> marks = new ArrayList<>();
        repository.findAll().forEach(mark -> marks.add(new Mark(mark.getId(), mark.getDate(), mark.getValue(),
                mark.getStudent(), mark.getSubject())));
        return marks;
    }

    public Mark getMark(Integer id) {
        return repository.findById(id).map(mark -> new Mark(mark.getId(), mark.getDate(), mark.getValue(),
                mark.getStudent(), mark.getSubject())).orElse(new Mark());
    }

    public void save(Mark mark) {
        Student student = mark.getStudent();
        student.getMarks().add(mark);
        Mark currentMark = new Mark(mark.getId(), mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject());
        repository.save(currentMark);
    }

    public List<Mark> findByStudent(Student student) {
        List<Mark> marksWithStudent = new ArrayList<>();
        repository.findByStudent(student).forEach(mark -> marksWithStudent.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithStudent;
    }


    public List<Mark> findBySubject(Subject subject) {
        List<Mark> marksWithSubject = new ArrayList<>();
        repository.findBySubject(subject).forEach(mark -> marksWithSubject.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubject;
    }

    public List<Mark> findByStudentAndSubject(Student student, Subject subject) {
        List<Mark> marksWithStudentAndSubject = new ArrayList<>();
        repository.findByStudentAndSubject(student, subject).forEach(mark -> marksWithStudentAndSubject.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithStudentAndSubject;

    }

    public List<Mark> findByDateAfter(LocalDate fromDate) {
        List<Mark> marksByDateAfter = new ArrayList<>();
        repository.findByDateAfter(fromDate).forEach(mark -> marksByDateAfter.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksByDateAfter;
    }

    public List<Mark> findByDateBefore(LocalDate toDate) {
        List<Mark> marksByDateBefore = new ArrayList<>();
        repository.findByDateBefore(toDate).forEach(mark -> marksByDateBefore.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksByDateBefore;
    }

    public List<Mark> findByDateBetween(LocalDate fromDate, LocalDate toDate) {
        List<Mark> marksByDateBetween = new ArrayList<>();
        repository.findByDateBetween(fromDate, toDate).forEach(mark -> marksByDateBetween.add(new Mark(mark.getId(),
                mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksByDateBetween;
    }

    public List<Mark> findByStudentAndDateBefore(Student student, LocalDate toDate) {
        List<Mark> marksWithStudentAndDate = new ArrayList<>();
        repository.findByStudentAndDateBefore(student, toDate).forEach(mark ->
                marksWithStudentAndDate.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithStudentAndDate;
    }

    public List<Mark> findByStudentAndDateAfter(Student student, LocalDate fromDate) {
        List<Mark> marksWithStudentAndDate = new ArrayList<>();
        repository.findByStudentAndDateAfter(student, fromDate).forEach(mark ->
                marksWithStudentAndDate.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithStudentAndDate;
    }

    public List<Mark> findByStudentAndDateBetween(Student student, LocalDate fromDate, LocalDate toDate) {
        List<Mark> marksWithStudentAndDates = new ArrayList<>();
        repository.findByStudentAndDateBetween(student, fromDate, toDate).forEach(mark ->
                marksWithStudentAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithStudentAndDates;
    }

    public List<Mark> findBySubjectAndDateBefore(Subject subject, LocalDate toDate){
        List<Mark> marksWithSubjectAndDates = new ArrayList<>();
        repository.findBySubjectAndDateBefore(subject, toDate).forEach(mark ->
                marksWithSubjectAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubjectAndDates;
    }

    public List<Mark> findBySubjectAndDateAfter(Subject subject, LocalDate fromDate){
        List<Mark> marksWithSubjectAndDates = new ArrayList<>();
        repository.findBySubjectAndDateAfter(subject, fromDate).forEach(mark ->
                marksWithSubjectAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubjectAndDates;
    }

    public List<Mark> findBySubjectAndDateBetween(Subject subject, LocalDate fromDate, LocalDate toDate){
        List<Mark> marksWithSubjectAndDates = new ArrayList<>();
        repository.findBySubjectAndDateBetween(subject, fromDate, toDate).forEach(mark ->
                marksWithSubjectAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubjectAndDates;
    }

    public List<Mark> findByStudentAndSubjectAndDateBefore(Student student, Subject subject, LocalDate toDate){
        List<Mark> marksWithSubAndStudAndDates = new ArrayList<>();
        repository.findByStudentAndSubjectAndDateBefore(student, subject, toDate).forEach(mark ->
                marksWithSubAndStudAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubAndStudAndDates;
    }

    public List<Mark> findByStudentAndSubjectAndDateAfter(Student student, Subject subject, LocalDate fromDate){
        List<Mark> marksWithSubAndStudAndDates = new ArrayList<>();
        repository.findByStudentAndSubjectAndDateAfter(student, subject, fromDate).forEach(mark ->
                marksWithSubAndStudAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubAndStudAndDates;
    }

    public List<Mark> findByStudAndSubAndDateBetween(Student student, Subject subject, LocalDate fromDate, LocalDate toDate){
        List<Mark> marksWithSubAndStudAndDates = new ArrayList<>();
        repository.findByStudentAndSubjectAndDateBetween(student, subject, fromDate, toDate).forEach(mark ->
                marksWithSubAndStudAndDates.add(new Mark(mark.getId(),
                        mark.getDate(), mark.getValue(), mark.getStudent(), mark.getSubject())));
        return marksWithSubAndStudAndDates;
    }
}
