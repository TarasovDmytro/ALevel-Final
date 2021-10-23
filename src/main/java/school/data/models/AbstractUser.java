package school.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AbstractUser extends AbstractEntity {

    @JsonIgnore
    private String hashedPassword;

    @JsonIgnore
    @Size(min = 1, message = "The Username cannot be empty")
    private String username;

    @NotNull(message = "The Full Name cannot be empty")
    @Size(min = 1, message = "The Full Name cannot be empty")
    private String fullName;

    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public AbstractUser(Integer id, String fullName, String email, String phone, LocalDate dateOfBirth,
                        Set<Role> roles, String username, String hashedPassword) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public AbstractUser(String fullName, String email, String phone, LocalDate dateOfBirth, Set<Role> roles, String username) {
        super();
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
        this.username = username;
    }

    public AbstractUser(Integer id, String fullName, String email, String phone, LocalDate dateOfBirth, Set<Role> roles, String username) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbstractUser that = (AbstractUser) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
