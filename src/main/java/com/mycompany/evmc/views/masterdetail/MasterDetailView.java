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

@PageTitle("Xodimlar")
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

    private final Button cancel = new Button("Bekor qilish");
    private final Button save = new Button("Saqlash");

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
        grid.addColumn(EmployeeDto::getFirstName).setHeader("Ism").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getLastName).setHeader("Familiya").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getRole).setHeader("Lavozim").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHiredAt).setHeader("Ishga olingan sana").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHolidayStartDate).setHeader("Ta’til boshlanishi").setAutoWidth(true);
        grid.addColumn(EmployeeDto::getHolidayEndDate).setHeader("Ta’til tugashi").setAutoWidth(true);

        LitRenderer<EmployeeDto> onHolidayRenderer = LitRenderer.<EmployeeDto>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", emp -> emp.isOnHoliday() ? "check" : "minus")
                .withProperty("color", emp -> emp.isOnHoliday() ? "var(--lumo-primary-text-color)" : "var(--lumo-disabled-text-color)");

        grid.addColumn(onHolidayRenderer).setHeader("Ta’tilda").setAutoWidth(true);

        List<EmployeeDto> employees = employeeService.getAllEmployees();
        dataProvider = new ListDataProvider<>(employees);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            EmployeeDto selected = event.getValue();
            if (selected != null) {
                populateForm(selected);
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, selected.getId()));
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
                Notification.show("Xodim topilmadi").addThemeVariants(NotificationVariant.LUMO_ERROR);
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

        firstName = new TextField("Ism");
        lastName = new TextField("Familiya");
        email = new TextField("Email");
        role = new TextField("Lavozim");
        hiredAt = new DatePicker("Ishga olingan sana");
        holidayStartDate = new DatePicker("Ta’til boshlanishi");
        holidayEndDate = new DatePicker("Ta’til tugashi");
        password = new PasswordField("Parol");
        onHoliday = new Checkbox("Ta’tilda");

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

            if (password.getValue() != null && !password.getValue().isEmpty()) {
                employeeDto.setPassword(passwordEncoder.encode(password.getValue()));
            } else if (employeeDto.getId() == null) {
                Notification.show("Yangi xodim uchun parol kiritish shart", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (employeeDto.isOnHoliday()) {
                LocalDate start = employeeDto.getHolidayStartDate();
                LocalDate end = employeeDto.getHolidayEndDate();

                if (start == null || end == null) {
                    Notification.show("Iltimos, ta’tilning boshlanish va tugash sanalarini kiriting", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (end.isBefore(start)) {
                    Notification.show("Ta’til tugash sanasi boshlanish sanasidan oldin bo‘lishi mumkin emas", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!employeeService.isHolidayEligible(employeeDto.getId(), start)) {
                    Notification.show("Xodim hali ta’til olish huquqiga ega emas (10 oylik qoidasi)", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                employeeService.startHoliday(employeeDto.getId(), start, end);
            } else if (employeeDto.getId() != null) {
                employeeService.endHoliday(employeeDto.getId());
            }

            if (employeeDto.getId() == null) {
                employeeService.createEmployee(employeeDto);
            } else {
                employeeService.updateEmployee(employeeDto.getId(), employeeDto);
            }

            clearForm();
            refreshGrid();
            Notification.show("Xodim ma’lumotlari saqlandi").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(MasterDetailView.class);

        } catch (ValidationException e) {
            Notification.show("Iltimos, maydonlarni tekshiring", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Kutilmagan xato: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
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
        this.employeeDto = value != null ? value : new EmployeeDto();
        binder.readBean(employeeDto);
        onHoliday.setValue(employeeDto.isOnHoliday());
        password.clear();
    }
}
