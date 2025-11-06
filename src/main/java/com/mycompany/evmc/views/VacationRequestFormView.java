package com.mycompany.evmc.views;

import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.VacationRequest;
import com.mycompany.evmc.model.VacationType;
import com.mycompany.evmc.security.SecurityUtils;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.service.VacationRequestService;
import com.mycompany.evmc.service.VacationTypeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;


@Route(value = "request-vacation", layout = MainLayout.class)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class VacationRequestFormView extends VerticalLayout {

    private final VacationRequestService vacationRequestService;
    private final VacationTypeService vacationTypeService;
    private final EmployeeService employeeService;

    private Employee currentEmployee;

    @Autowired
    public VacationRequestFormView(VacationRequestService vacationRequestService,
                                   VacationTypeService vacationTypeService,
                                   EmployeeService employeeService) {
        this.vacationRequestService = vacationRequestService;
        this.vacationTypeService = vacationTypeService;
        this.employeeService = employeeService;

        setWidth("400px");
        setAlignItems(Alignment.CENTER);

        fetchCurrentEmployee();

        H3 title = new H3("Submit Vacation Request");

        FormLayout formLayout = new FormLayout();

        ComboBox<VacationType> typeCombo = new ComboBox<>("Vacation Type");
        typeCombo.setItems(vacationTypeService.getAllTypes());
        typeCombo.setItemLabelGenerator(VacationType::getDisplayName);

        DatePicker startDate = new DatePicker("Start Date");
        DatePicker endDate = new DatePicker("End Date");

        NumberField unitsField = new NumberField("Units (days)");
        unitsField.setMin(0.5);
        unitsField.setStep(0.5);

        ComboBox<String> unitTypeCombo = new ComboBox<>("Unit Type");
        unitTypeCombo.setItems("days", "hours");
        unitTypeCombo.setValue("days");

        TextArea reasonField = new TextArea("Reason");

        Button submitButton = new Button("Submit Request", e -> submitRequest(
                typeCombo, startDate, endDate, unitsField, unitTypeCombo, reasonField
        ));

        formLayout.add(typeCombo, startDate, endDate, unitsField, unitTypeCombo, reasonField);
        add(title, formLayout, submitButton);
    }

    private void fetchCurrentEmployee() {
        String username = SecurityUtils.getUsername();
        if (username != null) {
            currentEmployee = employeeService.findByEmail(username);
        } else {
            Notification.show("No logged-in user found", 3000, Notification.Position.MIDDLE);
        }
    }

    private void submitRequest(ComboBox<VacationType> typeCombo,
                               DatePicker startDate,
                               DatePicker endDate,
                               NumberField unitsField,
                               ComboBox<String> unitTypeCombo,
                               TextArea reasonField) {
        if (currentEmployee == null) {
            Notification.show("Cannot submit request: user not found", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (typeCombo.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || unitsField.isEmpty()) {
            Notification.show("Please fill all required fields", 3000, Notification.Position.MIDDLE);
            return;
        }

        VacationRequest request = VacationRequest.builder()
                .employee(currentEmployee)
                .type(typeCombo.getValue())
                .startTimestamp(startDate.getValue().atStartOfDay().atOffset(ZoneOffset.UTC))
                .endTimestamp(endDate.getValue().atStartOfDay().atOffset(ZoneOffset.UTC))
                .units(unitsField.getValue())
                .unitType(unitTypeCombo.getValue())
                .reason(reasonField.getValue())
                .status("pending")
                .build();

        vacationRequestService.createRequest(request);
        Notification.show("Vacation request submitted!", 3000, Notification.Position.MIDDLE);

        // clear form
        typeCombo.clear();
        startDate.clear();
        endDate.clear();
        unitsField.clear();
        unitTypeCombo.setValue("days");
        reasonField.clear();
    }
}
