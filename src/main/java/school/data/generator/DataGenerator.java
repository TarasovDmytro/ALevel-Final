package school.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.data.models.Role;
import school.data.models.entity.Admin;
import school.data.service.AdminService;

import java.util.Collections;
import java.util.HashSet;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData (@Autowired PasswordEncoder passwordEncoder, @Autowired AdminService adminService) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (adminService.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating admin entity");

            Admin admin = new Admin("Name LastName", "admin.email@email.com", "+380660677919",
                    null, new HashSet<>(Collections.singleton(Role.ADMIN)), "admin");
            adminService.save(admin);
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            adminService.save(admin);

            logger.info("Generated admin entity with username = 'admin' and password - 'admin'");
        };
    }
}