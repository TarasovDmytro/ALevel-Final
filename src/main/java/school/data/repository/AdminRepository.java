package school.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.data.models.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByUsername(String username);
}
