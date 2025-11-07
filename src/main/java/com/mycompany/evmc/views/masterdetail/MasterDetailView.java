package com.mycompany.evmc.views.masterdetail;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Employees")
@Route(value = "employees/:employeeID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Menu(order = 1, icon = LineAwesomeIconUrl.USERS_SOLID)
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private static final String EMPLOYEE_ID = "employeeID";
    private static final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "employees/%s/edit";

    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private ListDataProvider<EmployeeDto> dataProvider;

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField role;
    private DatePicker hiredAt;
    private DatePicker holidayStartDate;
    private DatePicker holidayEndDate;
    private PasswordField password;
    private Checkbox onHoliday;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<EmployeeDto> binder;
    private EmployeeDto employeeDto;
    private final EmployeeService employeeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MasterDetailView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        addClassName("employee-master-detail-view");
        setSizeFull();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);

        configureGrid();

        binder = new BeanValidationBinder<>(EmployeeDto.class);
        binder.forField(onHoliday).bind(EmployeeDto::isOnHoliday, EmployeeDto::setOnHoliday);
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> saveEmployee());
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        grid.setSizeFull();
        wrapper.add(grid);
        splitLayout.addToPrimary(wrapper);
    }

    private void configureGrid() {
        grid.addColumn(EmployeeDto::getFirstName).setHeader("First Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getLastName).setHeader("Last Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getRole).setHeader("Role").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHiredAt).setHeader("Hired At").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHolidayStartDate).setHeader("Holiday Start").setAutoWidth(true);
        grid.addColumn(EmployeeDto::getHolidayEndDate).setHeader("Holiday End").setAutoWidth(true);

        LitRenderer<EmployeeDto> onHolidayRenderer = LitRenderer.<EmployeeDto>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", emp -> emp.isOnHoliday() ? "check" : "minus")
                .withProperty("color", emp -> emp.isOnHoliday() ? "var(--lumo-primary-text-color)" : "var(--lumo-disabled-text-color)");

        grid.addColumn(onHolidayRenderer).setHeader("On Holiday").setAutoWidth(true);

        List<EmployeeDto> employees = employeeService.getAllEmployees();
        dataProvider = new ListDataProvider<>(employees);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                populateForm(event.getValue());
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> employeeID = event.getRouteParameters().get(EMPLOYEE_ID).map(UUID::fromString);
        employeeID.ifPresent(id -> {
            try {
                EmployeeDto emp = employeeService.getEmployeeById(id);
                populateForm(emp);
            } catch (Exception ex) {
                Notification.show("Employee not found").addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayout = new Div();
        editorLayout.setClassName("editor-layout");
        editorLayout.setWidth("400px");
        editorLayout.getStyle().set("padding", "var(--lumo-space-m)");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();

        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        role = new TextField("Role");
        hiredAt = new DatePicker("Hired At");
        holidayStartDate = new DatePicker("Holiday Start");
        holidayEndDate = new DatePicker("Holiday End");
        password = new PasswordField("Password");
        onHoliday = new Checkbox("On Holiday");

        formLayout.add(firstName, lastName, email, role, hiredAt, holidayStartDate, holidayEndDate, password, onHoliday);
        editorLayout.add(formLayout);
        createButtonLayout(editorLayout);

        splitLayout.addToSecondary(editorLayout);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout layout = new HorizontalLayout();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(save, cancel);
        editorLayoutDiv.add(layout);
    }

    private void saveEmployee() {
        if (employeeDto == null) {
            employeeDto = new EmployeeDto();
        }

        try {
            binder.writeBean(employeeDto);

            // Password handling with hashing
            if (password.getValue() != null && !password.getValue().isEmpty()) {
                employeeDto.setPassword(passwordEncoder.encode(password.getValue()));
            } else if (employeeDto.getId() == null) {
                Notification.show("Password required for new employee", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Holiday logic
            if (employeeDto.isOnHoliday()) {
                LocalDate start = employeeDto.getHolidayStartDate();
                LocalDate end = employeeDto.getHolidayEndDate();

                if (start == null || end == null) {
                    Notification.show("Please set both holiday start and end dates", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // NEW: Validate that end date is after start date
                if (end.isBefore(start)) {
                    Notification.show("Holiday end date cannot be before start date", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!employeeService.isHolidayEligible(employeeDto.getId(), start)) {
                    Notification.show("Employee is not eligible for holiday yet (10-month rule)", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                employeeService.startHoliday(employeeDto.getId(), start, end);
            } else if (employeeDto.getId() != null) {
                employeeService.endHoliday(employeeDto.getId());
            }


            // Create or update
            if (employeeDto.getId() == null) {
                employeeService.createEmployee(employeeDto);
            } else {
                employeeService.updateEmployee(employeeDto.getId(), employeeDto);
            }

            clearForm();
            refreshGrid();
            Notification.show("Employee saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(MasterDetailView.class);

        } catch (ValidationException e) {
            Notification.show("Check the form fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Unexpected error: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(employeeService.getAllEmployees());
        dataProvider.refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(EmployeeDto value) {
        if (value == null) {
            this.employeeDto = new EmployeeDto();
        } else {
            this.employeeDto = value;
        }
        binder.readBean(employeeDto);
        onHoliday.setValue(employeeDto.isOnHoliday());
        password.clear();
    }
}
