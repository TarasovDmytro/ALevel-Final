package school.data.models.entity;

import com.vaadin.fusion.Nonnull;
import lombok.*;
import org.hibernate.Hibernate;
import school.data.models.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Mark extends AbstractEntity {

    private LocalDate date;
    private Integer value;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Mark(@Nonnull Integer id, LocalDate date, Integer value, Student student, Subject subject) {
        super(id);
        this.date = date;
        this.value = value;
        this.student = student;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Mark mark = (Mark) o;
        return Objects.equals(getId(), mark.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
