package school.views.adminViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
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
import school.data.models.entity.Subject;
import school.data.models.entity.Teacher;
import school.data.service.SubjectService;
import school.data.service.TeacherService;
import school.views.basicViewForms.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@PageTitle("Subjects")
@Route(value = "subject/:subjectID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
public class SubjectsView extends Div implements BeforeEnterObserver {

    private final String SUBJECT_ID = "subjectID";
    private final String SUBJECT_EDIT_ROUTE_TEMPLATE = "subject/%d/edit";

    private final Grid<Subject> grid = new Grid<>(Subject.class, false);

    private final TextField id = new TextField("Id");
    private final TextField subjectName = new TextField("Subject Name");

    private final ComboBox<Teacher> teacher = new ComboBox<>("Teacher");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Subject> binder;
    private final Div editorLayoutDiv = new Div();

    private Subject subject;

    private final SubjectService subjectService;
    private final TeacherService teacherService;

    public SubjectsView(@Autowired SubjectService subjectService, @Autowired TeacherService teacherService) {
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        addClassNames("subjects-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("subjectName").setAutoWidth(true);
        grid.addColumn("teacher").setAutoWidth(true);

        grid.setItems(query -> subjectService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                id.setVisible(true);
                UI.getCurrent().navigate(String.format(SUBJECT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SubjectsView.class);
            }
        });

        binder = new BeanValidationBinder<>(Subject.class);

        binder.forField(id).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("id");

        binder.bindInstanceFields(this);

        delete.addClickListener(e -> {
            if (this.subject == null) {
                this.subject = new Subject();
            }
            try {
                binder.writeBean(this.subject);
                if (this.subject.getId() == null) {
                    Notification.show("The subject must have an ID");
                } else {
                    subjectService.delete(this.subject.getId());
                    afterEditAction("The subject " + subject.getSubjectName() + " deleted.");
                }
            } catch (ValidationException ex) {
                Notification.show("An exception happened while trying to delete the subject details.");
            }
        });

        cancel.addClickListener(e -> afterEditAction("Subject details store canceled."));

        save.addClickListener(e -> {
            try {
                if (this.subject == null) {
                    this.subject = new Subject();
                }
                binder.writeBean(this.subject);

                subjectService.save(this.subject);
                afterEditAction("Subject details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the subject details.");
            }
        });

    }

    private void afterEditAction(String notification) {
        id.setVisible(false);
        clearForm();
        refreshGrid();
        Notification.show(notification);
        UI.getCurrent().navigate(SubjectsView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> subjectId = event.getRouteParameters().getInteger(SUBJECT_ID);
        if (subjectId.isPresent()) {
            Optional<Subject> subjectFromBackend = subjectService.get(subjectId.get());
            if (subjectFromBackend.isPresent()) {
                populateForm(subjectFromBackend.get());
            } else {
                Notification.show(String.format("The requested subject was not found, ID = %d", subjectId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(SubjectsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("500px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);
        editorDiv.add(createFormLayout());

        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        id.setVisible(false);
        teacher.setPlaceholder("choose a teacher");
        teacher.setItems(teacherService.findAll());
        Component[] fields = new Component[]{id, subjectName, teacher};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        return formLayout;
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel, delete);
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

    private void populateForm(Subject value) {
        this.subject = value;
        binder.readBean(this.subject);
    }
}
