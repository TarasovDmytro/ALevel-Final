package school.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import school.data.models.entity.Subject;
import school.data.repository.SubjectRepository;

import java.util.List;

@Service
public class SubjectService extends CrudService<Subject, Integer> {

    private final SubjectRepository repository;

    public SubjectService(@Autowired SubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SubjectRepository getRepository() {
        return repository;
    }

    public List<Subject> findAll(){
        return repository.findAll();
    }

    public void save(Subject subject) {
        Subject currentSubject = new Subject(subject.getId(), subject.getSubjectName(), subject.getTeacher());
        repository.save(currentSubject);
    }
}
