package school.views.studentsViews;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import school.data.models.entity.Student;
import school.data.service.StudentService;
import school.views.basicViewForms.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Students")
@Route(value = "student-views/students", layout = MainLayout.class)
@RolesAllowed({"student", "teacher"})
public class StudentsView extends Div {

    public StudentsView(@Autowired StudentService studentService) {
        addClassNames("students-view", "flex", "flex-col", "h-full");

        Grid<Student> grid = new Grid<>(Student.class, false);
        add(grid);

        grid.addColumn("fullName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("rankFirstSemester").setAutoWidth(true);
        grid.addColumn("rankSecondSemester").setAutoWidth(true);

        grid.setItems(query -> studentService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();
    }
}

