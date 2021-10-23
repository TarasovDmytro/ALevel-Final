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
import com.vaadin.flow.component.listbox.MultiSelectListBox;
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
import school.data.models.entity.Teacher;
import school.data.service.TeacherService;
import school.views.basicViewForms.MainLayout;
import school.views.basicViewForms.PhoneNumberField;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@PageTitle("Teachers")
@Route(value = "teachers/:teacherID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
public class TeachersView extends Div implements BeforeEnterObserver {

    private final String TEACHER_ID = "teacherID";
    private final String TEACHER_EDIT_ROUTE_TEMPLATE = "teachers/%d/edit";

    private final Grid<Teacher> grid = new Grid<>(Teacher.class, false);

    private final TextField id = new TextField("ID");
    private final TextField fullName = new TextField("Full name");
    private final EmailField email = new EmailField("Email address");
    private final DatePicker dateOfBirth = new DatePicker("Birthday");
    private final PhoneNumberField phone = new PhoneNumberField("Phone number");
    private final MultiSelectListBox<Role> role = new MultiSelectListBox<>();
    private final TextField passwordField = new TextField("Password (4 - 12 characters)");
    private final TextField username = new TextField("Username");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button setPassword = new Button("Set Pass");

    private final BeanValidationBinder<Teacher> binder = new BeanValidationBinder<>(Teacher.class);

    private final Div editorLayoutDiv = new Div();

    private Teacher teacher;
    private String currentPassword = "";
    private final TeacherService teacherService;

    public TeachersView(@Autowired TeacherService teacherService) {
        this.teacherService = teacherService;
        addClassNames("teachers-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createEditorLayout(splitLayout);
        createGridLayout(splitLayout);

        add(splitLayout);

        grid.addColumn("fullName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);

        grid.setItems(query -> teacherService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                passwordField.setVisible(false);
                setPassword.setVisible(true);
                UI.getCurrent().navigate(String.format(TEACHER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeachersView.class);
            }
        });

        binder.forField(id).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("id");
        binder.forField(passwordField).withNullRepresentation("").bind("hashedPassword");
        binder.bindInstanceFields(this);

        setPassword.addClickListener(e -> {
            if (!passwordField.isVisible()) passwordField.setVisible(true);
        });

        delete.addClickListener(e -> {
            if (this.teacher == null) {
                this.teacher = new Teacher();
            }
            try {
                binder.writeBean(this.teacher);
                if (this.teacher.getId() == null) {
                    Notification.show("The teacher must have an ID");
                } else {
                    teacherService.delete(this.teacher.getId());
                    afterEditAction("The teacher " + teacher.getFullName() + " deleted");
                }
            } catch (ValidationException ex) {
                Notification.show("An exception happened while trying to delete the teacher details.");
            }
        });

        cancel.addClickListener(e -> afterEditAction("Teacher details store canceled"));

        save.addClickListener(e -> {
            try {
                if (this.teacher == null) {
                    this.teacher = new Teacher();
                }
                binder.writeBean(this.teacher);
                if (validatePassword(this.teacher)) {
                    this.teacher.getRoles().addAll(role.getValue().stream().toList());
                    teacherService.save(this.teacher);
                    afterEditAction("Teacher details stored.");
                } else Notification.show("Incorrect password");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the teacher details.");
            }
        });
    }

    private Boolean validatePassword(Teacher teacher) {
        if (teacher.getId() == null && teacher.getHashedPassword() == null)
            return false;
        if (teacher.getHashedPassword() == null) {
            teacher.setHashedPassword(currentPassword);
            return true;
        }
        else if (teacher.getHashedPassword().length() > 12) {
            return false;
        }
        return teacher.getHashedPassword().length() >= 4;
    }

    private void afterEditAction(String notification) {
        id.setVisible(false);
        clearForm();
        refreshGrid();
        Notification.show(notification);
        UI.getCurrent().navigate(TeachersView.class);
        if (!passwordField.isVisible()) passwordField.setVisible(true);
        if (setPassword.isVisible()) setPassword.setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> teacherId = event.getRouteParameters().getInteger(TEACHER_ID);
        if (teacherId.isPresent()) {
            Optional<Teacher> teacherFromBackend = teacherService.get(teacherId.get());
            if (teacherFromBackend.isPresent()) {
                Teacher teacher = teacherFromBackend.get();
                currentPassword = teacher.getHashedPassword();
                teacher.setHashedPassword("");
                populateForm(teacher);
            } else {
                Notification.show(String.format("The requested teacher was not found, ID = %d", teacherId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(TeachersView.class);
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
        role.setItems(Role.ADMIN, Role.TEACHER);
        formLayout.add(id, fullName, email, dateOfBirth, phone, username, passwordField, role);
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

    private void populateForm(Teacher value) {
        this.teacher = value;
        binder.readBean(this.teacher);
    }
}
