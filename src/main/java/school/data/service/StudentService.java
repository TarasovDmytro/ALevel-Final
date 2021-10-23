package school.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.vaadin.artur.helpers.CrudService;
import school.data.models.Role;
import school.data.models.entity.Student;
import school.data.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService extends CrudService<Student, Integer> {
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository repository;

    public StudentService(@Autowired PasswordEncoder passwordEncoder, @Autowired StudentRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    protected StudentRepository getRepository() {
        return repository;
    }


    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        repository.findAll().forEach(student -> students.add(new Student(student.getId(), student.getFullName(),
                student.getEmail(), student.getPhone(), student.getDateOfBirth(),
                student.getRoles(), student.getUsername(), student.getHashedPassword(), student.getMarks())));
        return students;
    }

    public void save(@Validated Student student) {
        Student currentStudent = new Student(student.getId(), student.getFullName(), student.getEmail(),
                student.getPhone(), student.getDateOfBirth(), student.getRoles(), student.getUsername(), student.getMarks());
        String password = student.getHashedPassword();
        if (password != null && password.length() < 12)
            password = passwordEncoder.encode(password);
        currentStudent.setHashedPassword(password);
        if (currentStudent.getRoles().isEmpty()) currentStudent.getRoles().add(Role.STUDENT);
        repository.save(currentStudent);
    }

    public Student findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public String getCurrentPassword(Integer userId) {
        Student student = repository.getById(userId);
        Student currentStudent = new Student(student.getId(), student.getFullName(), student.getEmail(),
                student.getPhone(), student.getDateOfBirth(), student.getRoles(), student.getUsername(),
                student.getHashedPassword(), student.getMarks());
        return currentStudent.getHashedPassword();
    }
}
