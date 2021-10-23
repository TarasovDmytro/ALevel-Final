package school.data.models.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.data.models.AbstractUser;
import school.data.models.Role;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends AbstractUser {
    public Admin(String fullName, String email, String phone, LocalDate dateOfBirth, Set<Role> roles, String username) {
        super(fullName, email, phone, dateOfBirth, roles, username);
    }
}
