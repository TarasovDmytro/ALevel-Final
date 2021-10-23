package school.views.loginAndRegViews;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@PageTitle("Login")
@Route(value = "login")
public class LoginFormView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginFormView(){
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        login.setForgotPasswordButtonVisible(false);

        login.setAction("login");

        add(new H1("Class Journal"), login);
        add(createFormLayout());
    }

    private HorizontalLayout createFormLayout() {
        HorizontalLayout linkLayout = new HorizontalLayout();
        linkLayout.setAlignItems(Alignment.CENTER);
        linkLayout.addClassName("link-layout");
        Anchor regLink = new Anchor(RouteConfiguration.forSessionScope().getUrl(RegistrationFormView.class), "Registration");
        linkLayout.add(regLink);
        return linkLayout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}