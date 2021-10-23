package school.data.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import school.data.models.AbstractEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Subject extends AbstractEntity {

    private String subjectName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Subject(Integer id, String subjectName, Teacher teacher) {
        super(id);
        this.subjectName = subjectName;
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Subject subject = (Subject) o;
        return Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return subjectName ;
    }
}
