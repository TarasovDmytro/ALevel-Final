package school.views.basicViewForms;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Class Journal")
@Route(value = "main-view", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class MainView extends Div {

    public MainView(){
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        Image image = new Image("images/class-journal.png", "Class Journal");
        image.setWidthFull();
        image.setHeightFull();
        layout.add(image);

        add(layout);
    }

}
