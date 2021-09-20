package school.data.service;

import school.data.entity.Mark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import java.time.LocalDate;

@Service
public class MarkService extends CrudService<Mark, Integer> {

    private MarkRepository repository;

    public MarkService(@Autowired MarkRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MarkRepository getRepository() {
        return repository;
    }

}
