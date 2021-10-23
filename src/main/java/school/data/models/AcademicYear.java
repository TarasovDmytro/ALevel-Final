package school.data.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AcademicYear {

    private static AcademicYear academicYear;

    private LocalDate startDate;
    private LocalDate finishDate;

    private AcademicYear() {
        this.startDate = LocalDate.MIN;
        this.finishDate = LocalDate.MAX;
    }

    public static AcademicYear getAcademicYear() {
        if (academicYear == null) {
            academicYear = new AcademicYear();
        }
        return academicYear;
    }
}
