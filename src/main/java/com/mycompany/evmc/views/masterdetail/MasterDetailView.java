package com.mycompany.evmc.views.masterdetail;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Employees")
@Route("employees/:employeeID?/:action?(edit)")
@RolesAllowed({"ROLE_ADMIN"})
@Menu(order = 1, icon = LineAwesomeIconUrl.USERS_SOLID)
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String EMPLOYEE_ID = "employeeID";
    private final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "employees/%s/edit";

    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField password;
    private TextField email;
    private TextField team;
    private TextField role;
    private DatePicker hiredAt;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<EmployeeDto> binder;

    private EmployeeDto employeeDto;

    private final EmployeeService employeeService;

    public MasterDetailView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        addClassName("employee-master-detail-view");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);
        setSizeFull();

        // Configure Grid columns
        grid.addColumn(EmployeeDto::getFirstName).setHeader("First Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getLastName).setHeader("Last Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getTeam).setHeader("Team").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getRole).setHeader("Role").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHiredAt).setHeader("Hired At").setAutoWidth(true).setSortable(true);

        // Use in-memory ListDataProvider (sorting works automatically)
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        ListDataProvider<EmployeeDto> dataProvider = new ListDataProvider<>(employees);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

        // Grid selection
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });

        // Binder setup
        binder = new BeanValidationBinder<>(EmployeeDto.class);
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.employeeDto != null && this.employeeDto.getId() != null) {
                    binder.writeBean(this.employeeDto);

                    // Only update password if it is set (not empty)
                    String pwd = password.getValue();
                    if (pwd != null && !pwd.isEmpty()) {
                        employeeDto.setPassword(pwd); // make sure EmployeeDto has setPassword()
                    }

                    employeeService.updateEmployee(employeeDto.getId(), employeeDto);
                } else {
                    EmployeeDto newEmployee = new EmployeeDto();
                    binder.writeBean(newEmployee);

                    // Set password for new employee
                    String pwd = password.getValue();
                    if (pwd != null && !pwd.isEmpty()) {
                        newEmployee.setPassword(pwd);
                    } else {
                        Notification.show("Password is required for new employee", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }

                    employeeService.createEmployee(newEmployee);
                }

                clearForm();
                refreshGrid();
                Notification.show("Employee saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(MasterDetailView.class);

            } catch (ValidationException ex) {
                Notification.show("Check the form fields", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> employeeID = event.getRouteParameters().get(EMPLOYEE_ID).map(UUID::fromString);
        employeeID.ifPresent(id -> {
            try {
                EmployeeDto employee = employeeService.getEmployeeById(id);
                populateForm(employee);
            } catch (Exception ex) {
                Notification.show("Employee not found").addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        // Container for the editor
        Div editorLayout = new Div();
        editorLayout.setClassName("editor-layout");
        editorLayout.setWidth("400px");
        editorLayout.getStyle().set("padding", "var(--lumo-space-m)");

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();

        // Create fields
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        team = new TextField("Team");
        role = new TextField("Role");
        hiredAt = new DatePicker("Hired At");
        password = new TextField("Password"); // or new PasswordField("Password") if you want masked input

        // Make each field full width
        firstName.setWidthFull();
        lastName.setWidthFull();
        email.setWidthFull();
        team.setWidthFull();
        role.setWidthFull();
        hiredAt.setWidthFull();
        password.setWidthFull();

        // Add fields to form
        formLayout.add(firstName, lastName, email, team, role, hiredAt, password);

        editorLayout.add(formLayout);
        createButtonLayout(editorLayout);

        splitLayout.addToSecondary(editorLayout);
    }


    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(save, cancel);
        editorLayoutDiv.add(layout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        grid.setSizeFull();
        wrapper.add(grid);
        splitLayout.addToPrimary(wrapper);
        splitLayout.setSizeFull();
    }

    private void refreshGrid() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        grid.setItems(employees);
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(EmployeeDto value) {
        this.employeeDto = value;
        binder.readBean(this.employeeDto);
    }
}
