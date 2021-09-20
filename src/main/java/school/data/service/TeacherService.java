package school.data.service;

import school.data.entity.Teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class TeacherService extends CrudService<Teacher, Integer> {

    private TeacherRepository repository;

    public TeacherService(@Autowired TeacherRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TeacherRepository getRepository() {
        return repository;
    }

}
