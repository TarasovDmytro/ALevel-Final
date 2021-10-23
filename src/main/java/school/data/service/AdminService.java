package school.data.service;

import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;
import school.data.models.entity.Admin;
import school.data.repository.AdminRepository;

@Service
public class AdminService extends CrudService<Admin, Integer> {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    @Override
    protected AdminRepository getRepository() {
        return repository;
    }

    public void save (Admin admin){
        repository.save(admin);
    }

    public Admin findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
