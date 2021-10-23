package school.views.teacherViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import school.data.models.entity.Mark;
import school.data.models.entity.Student;
import school.data.models.entity.Subject;
import school.data.service.MarkService;
import school.data.service.StudentService;
import school.data.service.SubjectService;
import school.views.basicViewForms.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@PageTitle("Marks")
@Route(value = "teacher-views/marks/:markID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"teacher"})
public class TeacherMarksView extends Div implements BeforeEnterObserver {
    private final SubjectService subjectService;
    private final MarkService markService;
    private final StudentService studentService;
    private Mark mark;

    private final String MARK_EDIT_ROUTE_TEMPLATE = "teacher-views/marks/%d/edit";

    private final ComboBox<Student> filterByStudent = new ComboBox<>();
    private final ComboBox<Subject> filterBySubject = new ComboBox<>();
    private final DatePicker filterFromDate = new DatePicker();
    private final DatePicker filterToDate = new DatePicker();

    private final Button noFilters = new Button("Delete filters");

    private final Grid<Mark> grid = new Grid<>(Mark.class, false);

    private final TextField id = new TextField("Id");
    private final ComboBox<Subject> subject = new ComboBox<>("Subject");
    private final ComboBox<Student> student = new ComboBox<>("Student");
    private final DatePicker date = new DatePicker("Date");
    private final TextField value = new TextField("Value of mark from 1 to 12");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Mark> binder = new BeanValidationBinder<>(Mark.class);
    private final Div editorLayoutDiv = new Div();

    public TeacherMarksView(@Autowired MarkService markService, @Autowired SubjectService subjectService,
                            @Autowired StudentService studentService) {
        this.markService = markService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        addClassNames("teacher-marks-view", "flex", "flex-col", "h-full");

        // Create UI
        HorizontalLayout filters = new HorizontalLayout();
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createFilterFormLayout(filters);
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(filters, splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("subject").setAutoWidth(true);
        grid.addColumn("student").setAutoWidth(true);
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("value").setAutoWidth(true);

        grid.setItems(query -> markService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                id.setVisible(true);
                UI.getCurrent().navigate(String.format(MARK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeacherMarksView.class);
            }
        });

        binder.forField(id)
                .withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .withNullRepresentation(0)
                .bind("id");
        binder.forField(value)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .withValidator((result, context) -> {
                    if (Integer.parseInt(value.getValue()) > 12 || Integer.parseInt(value.getValue()) < 1)
                        return ValidationResult.error("The value can only be in the range from 1 to 12");
                    return ValidationResult.ok();
                })
                .bind("value");
        binder.bindInstanceFields(this);

        delete.addClickListener(e -> {
            if (this.mark == null) {
                this.mark = new Mark();
            }
            try {
                binder.writeBean(this.mark);
                if (this.mark.getId() == null) {
                    Notification.show("The student must have an ID");
                } else {
                    markService.delete(this.mark.getId());

                    afterEditAction("The mark deleted");
                }
            } catch (ValidationException ex) {
                Notification.show("An exception happened while trying to delete the student details.");
            }
        });

        cancel.addClickListener(e -> afterEditAction("Mark details store canceled"));

        save.addClickListener(e -> {
            try {
                if (this.mark == null) {
                    this.mark = new Mark();
                }
                binder.writeBean(this.mark);
                this.mark.getStudent().getMarks().add(this.mark);
                studentService.save(this.mark.getStudent());
                markService.save(this.mark);
                List<Student> students = studentService.findAll();
                students.forEach(studentService::save);
                afterEditAction("Mark details stored.");

            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the mark details.");
            }
        });
    }

    private void afterEditAction(String notification) {
        clearForm();
        id.setVisible(false);
        refreshGrid();
        Notification.show(notification);
        UI.getCurrent().navigate(TeacherMarksView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String MARK_ID = "markID";
        Optional<Integer> markId = event.getRouteParameters().getInteger(MARK_ID);
        if (markId.isPresent()) {
            Optional<Mark> markFromBackend = markService.get(markId.get());
            if (markFromBackend.isPresent()) {
                populateForm(markFromBackend.get());
            } else {
                Notification.show(String.format("The requested mark was not found, ID = %d", markId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(TeacherMarksView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("500px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);
        editorDiv.add(createEditorFormLayout());
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private FormLayout createEditorFormLayout() {
        FormLayout formLayout = new FormLayout();

        id.setVisible(false);
        List<Subject> subjects = subjectService.findAll();
        List<Student> students = studentService.findAll();
        subject.setItems(subjects);
        subject.setPlaceholder("choose a subject");
        student.setItems(students);
        student.setPlaceholder("choose a student");
        Component[] fields = new Component[]{id, subject, student, date, value};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }

        formLayout.add(fields);
        return formLayout;
    }

    private void createFilterFormLayout(HorizontalLayout layout) {
        layout.add(filterByStudent);
        layout.add(filterBySubject);
        layout.add(filterFromDate);
        layout.add(filterToDate);
        layout.add(noFilters);
        layout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        layout.setSpacing(true);

        filterByStudent.setPlaceholder("filter by student");
        filterByStudent.setItems(studentService.findAll());
        filterByStudent.addValueChangeListener(e -> listByFilters(e.getValue(), filterBySubject.getValue(),
                filterFromDate.getValue(), filterToDate.getValue()));

        filterBySubject.setPlaceholder("filter by subject");
        filterBySubject.setItems(subjectService.findAll());
        filterBySubject.addValueChangeListener(e -> listByFilters(filterByStudent.getValue(), e.getValue(),
                filterFromDate.getValue(), filterToDate.getValue()));

        filterFromDate.setPlaceholder("from date");
        filterFromDate.addValueChangeListener(e -> listByFilters(filterByStudent.getValue(), filterBySubject.getValue(),
                e.getValue(), filterToDate.getValue()));

        filterToDate.setPlaceholder("to date");
        filterToDate.addValueChangeListener(e -> listByFilters(filterByStudent.getValue(), filterBySubject.getValue(),
                filterFromDate.getValue(), e.getValue()));


        noFilters.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        noFilters.addClickListener(e -> {
            filterBySubject.setValue(null);
            filterByStudent.setValue(null);
            filterFromDate.setValue(null);
            filterToDate.setValue(null);
            refreshGrid();
        });
    }

    private void listByFilters(Student student, Subject subject, LocalDate fromDate, LocalDate toDate) {
        if (student == null && subject == null && fromDate == null && toDate == null) {
            grid.setItems(markService.findAll());
            refreshGrid();
        }
        if (subject == null && fromDate == null && toDate == null)
            grid.setItems(markService.findByStudent(student));
        if (student == null && fromDate == null && toDate == null)
            grid.setItems(markService.findBySubject(subject));
        if (!(student == null) && !(subject == null) && fromDate == null && toDate == null)
            grid.setItems(markService.findByStudentAndSubject(student, subject));
        if (student == null && subject == null && fromDate == null && !(toDate == null))
            grid.setItems(markService.findByDateBefore(toDate));
        if (student == null && subject == null && !(fromDate == null) && toDate == null)
            grid.setItems(markService.findByDateAfter(fromDate));
        if (student == null && subject == null && !(fromDate == null) && !(toDate == null))
            grid.setItems(markService.findByDateBetween(fromDate, toDate));
        if (!(student == null) && subject == null && fromDate == null && !(toDate == null))
            grid.setItems(markService.findByStudentAndDateBefore(student, toDate));
        if (student == null && !(subject == null) && fromDate == null && !(toDate == null))
            grid.setItems(markService.findBySubjectAndDateBefore(subject, toDate));
        if (!(student == null) && !(subject == null) && fromDate == null && !(toDate == null))
            grid.setItems(markService.findByStudentAndSubjectAndDateBefore(student, subject, toDate));
        if (!(student == null) && subject == null && !(fromDate == null) && toDate == null)
            grid.setItems(markService.findByStudentAndDateAfter(student, fromDate));
        if (student == null && !(subject == null) && !(fromDate == null) && toDate == null)
            grid.setItems(markService.findBySubjectAndDateAfter(subject, fromDate));
        if (!(student == null) && !(subject == null) && !(fromDate == null) && toDate == null)
            grid.setItems(markService.findByStudentAndSubjectAndDateAfter(student, subject, fromDate));
        if (student == null && !(subject == null) && !(fromDate == null) && !(toDate == null))
            grid.setItems(markService.findBySubjectAndDateBetween(subject, fromDate, toDate));
        if (!(student == null) && subject == null && !(fromDate == null) && !(toDate == null))
            grid.setItems(markService.findByStudentAndDateBetween(student, fromDate, toDate));
        if (!(student == null) && !(subject == null) && !(fromDate == null) && !(toDate == null))
            grid.setItems(markService.findByStudAndSubAndDateBetween(student, subject, fromDate, toDate));
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
        populateForm(new Mark());
    }

    private void populateForm(Mark value) {
        this.mark = value;
        binder.readBean(this.mark);
    }
}
