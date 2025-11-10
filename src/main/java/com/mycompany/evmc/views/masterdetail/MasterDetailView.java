package com.mycompany.evmc.views.masterdetail;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.model.EmployeeRole;
import com.mycompany.evmc.model.Gender;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Xodimlar")
@Route(value = "employees/:employeeID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "MANAGER"})
@Menu(order = 1, icon = LineAwesomeIconUrl.USERS_SOLID)
@Uses(Grid.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private static final String EMPLOYEE_ID = "employeeID";
    private static final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "employees/%s/edit";

    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private ListDataProvider<EmployeeDto> dataProvider;

    private TextField firstName;
    private TextField lastName;
    private TextField surName;
    private Select<EmployeeRole> role;
    private DatePicker hiredAt;
    private Checkbox onHoliday;
    private DatePicker holidayStartDate;
    private DatePicker holidayEndDate;
    private TextField department;
    private Select<Gender> gender;

    private final Button cancel = new Button("Bekor qilish");
    private final Button delete = new Button("O'chirish");
    private final Button save = new Button("Saqlash");

    private final BeanValidationBinder<EmployeeDto> binder;
    private EmployeeDto employeeDto;
    private final EmployeeService employeeService;

    public MasterDetailView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        addClassName("employee-master-detail-view");
        setSizeFull();

        binder = new BeanValidationBinder<>(EmployeeDto.class); // initialize first

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout); // safe now
        add(splitLayout);

        configureGrid();

        binder.bindInstanceFields(this);
        binder.forField(onHoliday).bind(EmployeeDto::isOnHoliday, EmployeeDto::setOnHoliday);

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
        // First Name
        grid.addColumn(EmployeeDto::getFirstName)
                .setHeader("Ism")
                .setAutoWidth(true)
                .setSortable(true);

        // Last Name
        grid.addColumn(EmployeeDto::getLastName)
                .setHeader("Familiya")
                .setAutoWidth(true)
                .setSortable(true);

        // Sur Name
        grid.addColumn(EmployeeDto::getSurName)
                .setHeader("Otasining ismi")
                .setAutoWidth(true)
                .setSortable(true);

        // Department
        grid.addColumn(EmployeeDto::getDepartment)
                .setHeader("Bo‘lim")
                .setAutoWidth(true)
                .setSortable(true);

        // Gender
        grid.addColumn(emp -> emp.getGender() != null ? emp.getGender().name() : "")
                .setHeader("Jinsi")
                .setAutoWidth(true)
                .setSortable(true);


        // Role
        grid.addColumn(EmployeeDto::getEmployeeRole)
                .setHeader("Lavozim")
                .setAutoWidth(true)
                .setSortable(true);

        // Hired At
        grid.addColumn(EmployeeDto::getHiredAt)
                .setHeader("Ishga olingan sana")
                .setAutoWidth(true)
                .setSortable(true);

        // Holiday Start
        grid.addColumn(EmployeeDto::getHolidayStartDate)
                .setHeader("Ta’til boshlanishi")
                .setAutoWidth(true)
                .setSortable(true);

        // Holiday End
        grid.addColumn(EmployeeDto::getHolidayEndDate)
                .setHeader("Ta’til tugashi")
                .setAutoWidth(true)
                .setSortable(true);

        // New column: Days of holiday (between start and end)
        grid.addColumn(emp -> {
                    if (emp.getHolidayStartDate() != null && emp.getHolidayEndDate() != null) {
                        return emp.getHolidayEndDate().toEpochDay() - emp.getHolidayStartDate().toEpochDay();
                    }
                    return null;
                })
                .setHeader("davomiyligi")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator((e1, e2) -> {
                    Long d1 = (e1.getHolidayStartDate() != null && e1.getHolidayEndDate() != null)
                            ? e1.getHolidayEndDate().toEpochDay() - e1.getHolidayStartDate().toEpochDay() : null;
                    Long d2 = (e2.getHolidayStartDate() != null && e2.getHolidayEndDate() != null)
                            ? e2.getHolidayEndDate().toEpochDay() - e2.getHolidayStartDate().toEpochDay() : null;
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return -1;
                    if (d2 == null) return 1;
                    return d1.compareTo(d2);
                });

        // On Holiday column with icons + sorting
        LitRenderer<EmployeeDto> onHolidayRenderer = LitRenderer.<EmployeeDto>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' " +
                                "style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", emp -> emp.isOnHoliday() ? "check" : "minus")
                .withProperty("color", emp -> emp.isOnHoliday() ? "var(--lumo-success-color)" : "var(--lumo-disabled-text-color)");

        grid.addColumn(onHolidayRenderer)
                .setHeader("Ta’tilda")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(EmployeeDto::isOnHoliday); // sorting works

        // Next eligible holiday column
        grid.addColumn(emp -> {
                    if (emp.getHolidayEndDate() != null) {
                        return emp.getHolidayEndDate().plusMonths(10);
                    } else if (emp.getHiredAt() != null) {
                        return emp.getHiredAt().plusMonths(10);
                    } else {
                        return null;
                    }
                })
                .setHeader("Keyingi ta’til")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator((e1, e2) -> {
                    LocalDate d1 = e1.getHolidayEndDate() != null ? e1.getHolidayEndDate().plusMonths(10) :
                            e1.getHiredAt() != null ? e1.getHiredAt().plusMonths(10) : null;
                    LocalDate d2 = e2.getHolidayEndDate() != null ? e2.getHolidayEndDate().plusMonths(10) :
                            e2.getHiredAt() != null ? e2.getHiredAt().plusMonths(10) : null;
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return -1;
                    if (d2 == null) return 1;
                    return d1.compareTo(d2);
                });

        // Load data
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        dataProvider = new ListDataProvider<>(employees);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

        // Selection listener
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




    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayout = new Div();
        editorLayout.setClassName("editor-layout");
        editorLayout.setWidth("400px");
        editorLayout.getStyle().set("padding", "var(--lumo-space-m)");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();

        firstName = new TextField("Ism");
        lastName = new TextField("Familiya");
        surName = new TextField("Otasining ismi");
        department = new TextField("Bo'lim");
        gender = new Select<>();
        gender.setLabel("Jinsi");
        gender.setItems(Gender.values());
        role = new Select<>();
        role.setLabel("Lavozim");
        role.setItems(EmployeeRole.values());
        binder.forField(role)
                .asRequired("Lavozim tanlanishi shart")
                .bind(EmployeeDto::getEmployeeRole, EmployeeDto::setEmployeeRole);
        hiredAt = new DatePicker("Ishga olingan sana");
        onHoliday = new Checkbox("Ta’tilda");
        holidayStartDate = new DatePicker("Ta’til boshlanishi");
        holidayEndDate = new DatePicker("Ta’til tugashi");

        formLayout.add(firstName, lastName, surName, department, gender, role,
                hiredAt, onHoliday, holidayStartDate, holidayEndDate);
        editorLayout.add(formLayout);
        createButtonLayout(editorLayout);

        splitLayout.addToSecondary(editorLayout);
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
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout layout = new HorizontalLayout();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);  // red button for delete
        layout.add(save, cancel, delete);
        editorLayoutDiv.add(layout);

        // Delete click listener
        delete.addClickListener(e -> {
            if (employeeDto != null && employeeDto.getId() != null) {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("O'chirishni tasdiqlash");
                dialog.setText("Siz rostdan ham xodimni o‘chirmoqchimisiz?");
                dialog.setCancelable(true);

                dialog.setConfirmText("Ha, o'chirish");
                dialog.setCancelText("Bekor qilish");

                dialog.addConfirmListener(event -> {
                    employeeService.deleteEmployee(employeeDto.getId());
                    clearForm();
                    refreshGrid();
                    Notification.show("Xodim o‘chirildi").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    UI.getCurrent().navigate(MasterDetailView.class);
                });

                dialog.open();
            } else {
                Notification.show("Xodim tanlanmagan", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

    }


    private void saveEmployee() {
        if (employeeDto == null) {
            employeeDto = new EmployeeDto();
        }

        try {
            binder.writeBean(employeeDto);

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
    }
}
