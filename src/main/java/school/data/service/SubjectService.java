package school.data.service;

import school.data.entity.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SubjectService extends CrudService<Subject, Integer> {

    private SubjectRepository repository;

    public SubjectService(@Autowired SubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SubjectRepository getRepository() {
        return repository;
    }

}
