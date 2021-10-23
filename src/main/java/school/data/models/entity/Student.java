package school.data.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import school.data.models.AbstractUser;
import school.data.models.AcademicYear;
import school.data.models.Role;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Student extends AbstractUser {

    @OneToMany(mappedBy = "student", cascade = CascadeType.DETACH, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Mark> marks;

    private double rankFirstSemester;
    private double rankSecondSemester;

    public Student() {
        super();
        this.marks = new ArrayList<>();
        LocalDate startDate = AcademicYear.getAcademicYear().getStartDate();
        LocalDate finishDate = AcademicYear.getAcademicYear().getFinishDate();
        this.rankFirstSemester = Math.round(getAverageMarks(this.marks, startDate)*100.0)/100.0;
        this.rankSecondSemester = Math.round(getAverageMarks(this.marks, finishDate)*100.0)/100.0;
    }

    public Student(Integer id, String fullName, String email, String phone, LocalDate dateOfBirth,
                   Set<Role> roles, String username, List<Mark> marks) {
        super(id, fullName, email, phone, dateOfBirth, roles, username);
        this.marks = marks;
        LocalDate startDate = AcademicYear.getAcademicYear().getStartDate();
        LocalDate finishDate = AcademicYear.getAcademicYear().getFinishDate();
        this.rankFirstSemester = Math.round(getAverageMarks(this.marks, startDate)*100.0)/100.0;
        this.rankSecondSemester = Math.round(getAverageMarks(this.marks, finishDate)*100.0)/100.0;
    }

    public Student(Integer id, String fullName, String email, String phone, LocalDate dateOfBirth,
                   Set<Role> roles, String username, String hashedPassword, List<Mark> marks) {
        super(id, fullName, email, phone, dateOfBirth, roles, username, hashedPassword);
        this.marks = marks;
        LocalDate startDate = AcademicYear.getAcademicYear().getStartDate();
        LocalDate finishDate = AcademicYear.getAcademicYear().getFinishDate();
        this.rankFirstSemester = Math.round(getAverageMarks(this.marks, startDate)*100.0)/100.0;
        this.rankSecondSemester = Math.round(getAverageMarks(this.marks, finishDate)*100.0)/100.0;
    }

    private double getAverageMarks(List<Mark> marks, LocalDate semester) {
        return marks.stream()
                .filter(mark -> mark.getDate().getYear() == semester.getYear())
                .mapToDouble(Mark::getValue)
                .average().orElse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return Objects.equals(getId(), student.getId());
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
