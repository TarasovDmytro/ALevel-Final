package school.data.entity;

import javax.persistence.Entity;

import school.data.AbstractEntity;
import java.time.LocalDate;

@Entity
public class Mark extends AbstractEntity {

    private String subjectName;
    private String studentName;
    private LocalDate date;
    private Integer value;

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }

}
