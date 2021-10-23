package school.views.loginAndRegViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import school.data.models.entity.Student;
import school.data.service.StudentService;
import school.views.basicViewForms.MainLayout;
import school.views.basicViewForms.PhoneNumberField;

@PageTitle("Registration Form")
@Route(value = "registration-form", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class RegistrationFormView extends Div {

    private final TextField fullName = new TextField("Full name");
    private final EmailField email = new EmailField("Email address");
    private final DatePicker dateOfBirth = new DatePicker("Birthday");
    private final PhoneNumberField phone = new PhoneNumberField("Phone number");
    private final TextField username = new TextField("Username");
    private final PasswordField hashedPassword = new PasswordField("Password (4 - 12 characters)");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");


    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Student> binder = new BeanValidationBinder<>(Student.class);
    private Student student = new Student();

    public RegistrationFormView(@Autowired StudentService studentService) {
        addClassName("registration-form-view");
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(createTitle());
        mainLayout.add(createFormLayout());
        mainLayout.add(createButtonLayout());

        add(mainLayout);
        binder.forField(hashedPassword).withValidator(password -> password.length() >= 4 && password.length() <= 12,
                "Incorrect password").bind(Student::getHashedPassword, Student::setHashedPassword);
        binder.bindInstanceFields(this);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (this.student == null) {
                this.student = new Student();
            }
            try {
                if (!confirmPassword.getValue().equals(hashedPassword.getValue())) {
                    Notification.show("Password not confirmed");
                    return;
                }
                binder.writeBean(this.student);

            } catch (ValidationException ex) {
                ex.printStackTrace();
            }
            studentService.save(student);
            Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new Student());
        confirmPassword.setValue("");
    }

    private Component createTitle() {
        return new H3("Personal information");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setHeightFull();
        email.setErrorMessage("Please enter a valid email address");
        formLayout.add(fullName, dateOfBirth, username, phone, hashedPassword, email, confirmPassword);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
