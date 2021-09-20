package school.views.marks;

import java.util.Optional;

import school.data.entity.Mark;
import school.data.service.MarkService;

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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.component.textfield.TextField;

@PageTitle("Marks")
@Route(value = "marks/:markID?/:action?(edit)", layout = MainLayout.class)
public class MarksView extends Div implements BeforeEnterObserver {

    private final String MARK_ID = "markID";
    private final String MARK_EDIT_ROUTE_TEMPLATE = "marks/%d/edit";

    private Grid<Mark> grid = new Grid<>(Mark.class, false);

    private TextField id;
    private TextField subjectName;
    private TextField studentName;
    private DatePicker date;
    private TextField value;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Mark> binder;

    private Mark mark;

    private MarkService markService;

    public MarksView(@Autowired MarkService markService) {
        this.markService = markService;
        addClassNames("marks-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("subjectName").setAutoWidth(true);
        grid.addColumn("studentName").setAutoWidth(true);
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("value").setAutoWidth(true);
        grid.setItems(query -> markService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MARK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MarksView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Mark.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(id).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("id");
        binder.forField(value).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("value");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.mark == null) {
                    this.mark = new Mark();
                }
                binder.writeBean(this.mark);

                markService.update(this.mark);
                clearForm();
                refreshGrid();
                Notification.show("Mark details stored.");
                UI.getCurrent().navigate(MarksView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the mark details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> markId = event.getRouteParameters().getInteger(MARK_ID);
        if (markId.isPresent()) {
            Optional<Mark> markFromBackend = markService.get(markId.get());
            if (markFromBackend.isPresent()) {
                populateForm(markFromBackend.get());
            } else {
                Notification.show(String.format("The requested mark was not found, ID = %d", markId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MarksView.class);
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
        subjectName = new TextField("Subject Name");
        studentName = new TextField("Student Name");
        date = new DatePicker("Date");
        value = new TextField("Value");
        Component[] fields = new Component[]{id, subjectName, studentName, date, value};

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

    private void populateForm(Mark value) {
        this.mark = value;
        binder.readBean(this.mark);

    }
}
