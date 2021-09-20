package school.views.teachers;

import java.util.Optional;

import school.data.entity.Teacher;
import school.data.service.TeacherService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import school.views.MainLayout;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.component.textfield.TextField;

@PageTitle("Teachers")
@Route(value = "teachers/:teacherID?/:action?(edit)", layout = MainLayout.class)
public class TeachersView extends Div implements BeforeEnterObserver {

    private final String TEACHER_ID = "teacherID";
    private final String TEACHER_EDIT_ROUTE_TEMPLATE = "teachers/%d/edit";

    private Grid<Teacher> grid = new Grid<>(Teacher.class, false);

    private TextField id;
    private TextField fullName;
    private TextField subjectName;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Teacher> binder;

    private Teacher teacher;

    private TeacherService teacherService;

    public TeachersView(@Autowired TeacherService teacherService) {
        this.teacherService = teacherService;
        addClassNames("teachers-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("fullName").setAutoWidth(true);
        grid.addColumn("subjectName").setAutoWidth(true);
        grid.setItems(query -> teacherService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TEACHER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeachersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Teacher.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(id).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("id");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.teacher == null) {
                    this.teacher = new Teacher();
                }
                binder.writeBean(this.teacher);

                teacherService.update(this.teacher);
                clearForm();
                refreshGrid();
                Notification.show("Teacher details stored.");
                UI.getCurrent().navigate(TeachersView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the teacher details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> teacherId = event.getRouteParameters().getInteger(TEACHER_ID);
        if (teacherId.isPresent()) {
            Optional<Teacher> teacherFromBackend = teacherService.get(teacherId.get());
            if (teacherFromBackend.isPresent()) {
                populateForm(teacherFromBackend.get());
            } else {
                Notification.show(String.format("The requested teacher was not found, ID = %d", teacherId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TeachersView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        id = new TextField("Id");
        fullName = new TextField("Full Name");
        subjectName = new TextField("Subject Name");
        Component[] fields = new Component[]{id, fullName, subjectName};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Teacher value) {
        this.teacher = value;
        binder.readBean(this.teacher);

    }
}
