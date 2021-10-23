package school.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.vaadin.artur.helpers.CrudService;
import school.data.models.entity.Teacher;
import school.data.repository.TeacherRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService extends CrudService<Teacher, Integer> {
    private  final PasswordEncoder passwordEncoder;
    private final TeacherRepository repository;

    public TeacherService(@Autowired PasswordEncoder passwordEncoder, @Autowired TeacherRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    protected TeacherRepository getRepository() {
        return repository;
    }

    public List<Teacher> findAll() {
        List<Teacher> teachers = new ArrayList<>();
        repository.findAll().forEach(teacher -> teachers.add(new Teacher(teacher.getId(), teacher.getFullName(),
                teacher.getEmail(), teacher.getPhone(), teacher.getDateOfBirth(), teacher.getRoles(),
                teacher.getUsername(), teacher.getHashedPassword())));
        return teachers;
    }

    public void save(@Validated Teacher teacher) {
        Teacher currentTeacher = new Teacher(teacher.getId(), teacher.getFullName(), teacher.getEmail(),
                teacher.getPhone(), teacher.getDateOfBirth(), teacher.getRoles(), teacher.getUsername(),
                teacher.getHashedPassword());
        String password = teacher.getHashedPassword();
        if (password.length() <= 20){
            password = passwordEncoder.encode(password);
        }
        currentTeacher.setHashedPassword(password);
        repository.save(currentTeacher);
    }

    public Teacher findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
