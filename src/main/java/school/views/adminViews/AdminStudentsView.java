package school.views.adminViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import school.data.models.Role;
import school.data.models.entity.Student;
import school.data.service.StudentService;
import school.views.basicViewForms.MainLayout;
import school.views.basicViewForms.PhoneNumberField;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@PageTitle("Students")
@Route(value = "students/:studentID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
public class AdminStudentsView extends Div implements BeforeEnterObserver {

    private final String STUDENT_ID = "studentID";
    private final String STUDENT_EDIT_ROUTE_TEMPLATE = "students/%d/edit";

    private final Grid<Student> grid = new Grid<>(Student.class, false);

    private final TextField id = new TextField("ID");
    private final TextField fullName = new TextField("Full Name");
    private final EmailField email = new EmailField("Email address");
    private final DatePicker dateOfBirth = new DatePicker("Birthday");
    private final PhoneNumberField phone = new PhoneNumberField("Phone number");
    private final TextField passwordField = new TextField("Password (4 - 12 characters)");
    private final TextField username = new TextField("Username");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button setPassword = new Button("Set Pass");

    private final BeanValidationBinder<Student> binder = new BeanValidationBinder<>(Student.class);
    private final Div editorLayoutDiv = new Div();

    private Student student;
    private final StudentService studentService;
    private String currentPassword = "";

    public AdminStudentsView(@Autowired StudentService studentService) {
        this.studentService = studentService;
        addClassNames("students-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createEditorLayout(splitLayout);
        createGridLayout(splitLayout);

        add(splitLayout);

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

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                passwordField.setVisible(false);
                setPassword.setVisible(true);
                UI.getCurrent().navigate(String.format(STUDENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminStudentsView.class);
            }
        });

        binder.forField(id).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("id");
        binder.forField(passwordField).withNullRepresentation("").bind("hashedPassword");
        binder.bindInstanceFields(this);

        setPassword.addClickListener(e -> {
            if (!passwordField.isVisible()) passwordField.setVisible(true);
        });

        delete.addClickListener(e -> {
            if (this.student == null) {
                this.student = new Student();
            }
            try {
                binder.writeBean(this.student);
                if (this.student.getId() == null) {
                    Notification.show("The student must have an ID");
                } else {
                    studentService.delete(this.student.getId());
                    afterEditAction("The student " + student.getFullName() + " deleted");
                }
            } catch (ValidationException ex) {
                Notification.show("An exception happened while trying to delete the student details.");
            }
        });

        cancel.addClickListener(e -> afterEditAction("Student details store canceled."));

        save.addClickListener(e -> {
            try {
                if (student == null) {
                    student = new Student();
                }
                binder.writeBean(student);
                if (validatePassword(this.student)) {
                    this.student.getRoles().add(Role.STUDENT);
                    studentService.save(student);
                    afterEditAction("Student details stored.");
                } else Notification.show("Incorrect password");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the student details.");
            }
        });
    }

    private Boolean validatePassword(Student student) {
        if (student.getId() == null && student.getHashedPassword() == null)
            return false;
        if (student.getHashedPassword() == null) {
            student.setHashedPassword(currentPassword);
            return true;
        }
        else if (student.getHashedPassword().length() > 12) {
            return false;
        }
            return student.getHashedPassword().length() >= 4;
    }

    private void afterEditAction(String notification) {
        id.setVisible(false);
        clearForm();
        refreshGrid();
        Notification.show(notification);
        UI.getCurrent().navigate(AdminStudentsView.class);
        if (!passwordField.isVisible()) passwordField.setVisible(true);
        if (setPassword.isVisible()) setPassword.setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> studentId = event.getRouteParameters().getInteger(STUDENT_ID);
        if (studentId.isPresent()) {
            Optional<Student> studentFromBackend = studentService.get(studentId.get());
            if (studentFromBackend.isPresent()) {
                Student student = studentFromBackend.get();
                currentPassword = student.getHashedPassword();
                student.setHashedPassword("");
                populateForm(student);
            } else {
                Notification.show(String.format("The requested student was not found, ID = %d", studentId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(AdminStudentsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("700px");
        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);
        editorDiv.add(createFormLayout());
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        email.setErrorMessage("Please enter a valid email address");
        formLayout.add(id, fullName, email, dateOfBirth, phone, username, passwordField);
        id.setVisible(false);
        passwordField.setVisible(true);
        setPassword.setVisible(false);
        return formLayout;
    }


    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        setPassword.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        buttonLayout.add(save, cancel, delete, setPassword);
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

    private void populateForm(Student student) {
        this.student = student;
        binder.readBean(this.student);
    }
}
