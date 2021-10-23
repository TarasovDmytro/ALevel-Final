package school.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.data.models.AbstractUser;

@Service
public class AbstractUserService {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AdminService adminService;

    public AbstractUserService(@Autowired StudentService studentService, @Autowired TeacherService teacherService,
                               @Autowired AdminService adminService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.adminService = adminService;
    }

    public AbstractUser findByUsername(String username) {

        if (studentService.findByUsername(username)!=null){
            return studentService.findByUsername(username);
        } else {
        if (teacherService.findByUsername(username)!=null){
            return teacherService.findByUsername(username);
        } else {
        if (adminService.findByUsername(username)!=null){
            return adminService.findByUsername(username);
        }}}
        return null;
    }
}
