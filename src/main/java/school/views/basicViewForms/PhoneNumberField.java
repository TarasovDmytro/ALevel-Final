package school.views.basicViewForms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PhoneNumberField extends CustomField<String> {
    private final ComboBox<String> code = new ComboBox<>();
    private final TextField number = new TextField();

    public PhoneNumberField(String label) {
        setLabel(label);
        code.setWidth("100px");
        code.setPlaceholder("Code");
        code.setPattern("\\+\\d*");
        code.setPreventInvalidInput(true);
        code.setItems("+38050", "+38057", "+38063", "+38066", "+38067", "+38068", "+38093", "+38097", "+38099");
        code.addCustomValueSetListener(e -> code.setValue(e.getDetail()));
        number.setPattern("\\d*");
        number.setPreventInvalidInput(true);
        HorizontalLayout layout = new HorizontalLayout(code, number);
        layout.setFlexGrow(1.0, number);
        add(layout);
    }

    @Override
    protected String generateModelValue() {
        if (code.getValue() != null && number.getValue() != null) {
            return code.getValue() + " " + number.getValue();
        }
        return "";
    }

    @Override
    protected void setPresentationValue(String phoneNumber) {
        String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
        if (parts.length == 1) {
            code.clear();
            number.setValue(parts[0]);
        } else if (parts.length == 2) {
            code.setValue(parts[0]);
            number.setValue(parts[1]);
        } else {
            code.clear();
            number.clear();
        }
    }
}
