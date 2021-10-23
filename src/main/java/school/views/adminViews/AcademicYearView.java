package school.views.adminViews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import school.data.models.AcademicYear;
import school.data.models.entity.Student;
import school.data.service.StudentService;
import school.views.basicViewForms.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Academic year")
@Route(value = "academic-year/set", layout = MainLayout.class)
@RolesAllowed("admin")
public class AcademicYearView extends Div {

    private final DatePicker startDate = new DatePicker("Start of academic year");
    private final DatePicker finishDate = new DatePicker("Finish of academic year");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final Binder<AcademicYear> binder = new Binder<>(AcademicYear.class);

    private final AcademicYear academicYear;

    public AcademicYearView(@Autowired StudentService studentService) {
        this.academicYear = AcademicYear.getAcademicYear();
        addClassName("academic-year-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.setBean(academicYear);
        binder.bindInstanceFields(this);
        binder.forField(startDate).withValidator((result, context) -> {
            if (startDate.getValue() == null) return ValidationResult.error("The Start Date cannot be null");
            return ValidationResult.ok();
        }).bind("startDate");
        binder.forField(finishDate).withValidator((result, context) -> {
            if (finishDate.getValue() == null) return ValidationResult.error("The Finish Date cannot be null");
            return ValidationResult.ok();
        }).bind("finishDate");

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (startDate.getValue() != null && finishDate.getValue() != null &&
                    startDate.getValue().isBefore(finishDate.getValue()) &&
                    startDate.getValue().getYear() < finishDate.getValue().getYear()) {
                try {
                    binder.writeBean(academicYear);
                    List<Student> students = studentService.findAll();
                    students.forEach(studentService::save);
                    Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.", 2000,
                            Notification.Position.MIDDLE);
                    clearForm();
                } catch (ValidationException ex) {
                    ex.printStackTrace();
                }
            } else Notification.show("The dates cannot be null, the Start Date cannot be later than the End Date, and" +
                    " the years must be different.");
        });
    }

    private H3 createTitle() {
        return new H3("Academic Year");
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(startDate, finishDate);
        startDate.setValue(null);
        finishDate.setValue(null);
        return formLayout;
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void clearForm() {
        binder.readBean(AcademicYear.getAcademicYear());
    }
}
