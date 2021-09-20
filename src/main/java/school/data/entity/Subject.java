package school.data.entity;

import javax.persistence.Entity;

import school.data.AbstractEntity;

@Entity
public class Subject extends AbstractEntity {

    private String name;
    private String teacherName;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

}
