package school.data.models.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import school.data.models.AbstractUser;
import school.data.models.Role;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
public class Teacher extends AbstractUser {

    public Teacher(Integer id, String fullName, String email, String phone, LocalDate dateOfBirth, Set<Role> roles,
                   String username, String hashedPassword) {
        super(id, fullName, email, phone, dateOfBirth, roles, username, hashedPassword);
    }

    public Teacher() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(getId(), teacher.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return this.getFullName();
    }
}
