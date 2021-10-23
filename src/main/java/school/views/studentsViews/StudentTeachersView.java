package school.views.studentsViews;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import school.data.models.entity.Teacher;
import school.data.service.TeacherService;
import school.views.basicViewForms.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Teachers")
@Route(value = "student-views/teachers", layout = MainLayout.class)
@RolesAllowed("student")
public class StudentTeachersView extends Div {

    public StudentTeachersView (@Autowired TeacherService teacherService){
        addClassNames("teachers-view", "flex", "flex-col", "h-full");

        Grid<Teacher> grid = new Grid<>(Teacher.class, false);
        add(grid);

        grid.addColumn("fullName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);

        grid.setItems(query -> teacherService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();
    }
}
