package school.data.entity;

import javax.persistence.Entity;

import school.data.AbstractEntity;

@Entity
public class Teacher extends AbstractEntity {

    private String fullName;
    private String subjectName;

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

}
