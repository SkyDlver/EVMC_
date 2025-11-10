package com.mycompany.evmc.views.gridwithfilters;

import com.mycompany.evmc.dto.EmployeeDto;
import com.mycompany.evmc.model.EmployeeRole;
import com.mycompany.evmc.model.Gender;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Xodimlar Qidiruv Bo'limi")
@Route(value = "/grid-filters", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.FILTER_SOLID)
@Uses(Icon.class)
@RolesAllowed({"ADMIN", "MANAGER", "VIEWER"})
public class GridwithFiltersView extends Div {

    private final EmployeeService employeeService;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private final Filters filters;

    public GridwithFiltersView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setSizeFull();
        addClassNames("gridwith-filters-view");

        filters = new Filters(this::refreshGrid);

        VerticalLayout layout = new VerticalLayout(filters, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        configureGrid();
        refreshGrid();
    }

    private void configureGrid() {
        // Columns matching MasterDetail view
        grid.addColumn(EmployeeDto::getFirstName)
                .setHeader("Ism")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getLastName)
                .setHeader("Familiya")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getSurName)
                .setHeader("Otasining ismi")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getDepartment)
                .setHeader("Bo‘lim")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(emp -> emp.getGender() != null ? emp.getGender().name() : "")
                .setHeader("Jinsi")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getEmployeeRole)
                .setHeader("Lavozim")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getHiredAt)
                .setHeader("Ishga olingan sana")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getHolidayStartDate)
                .setHeader("Ta’til boshlanishi")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(EmployeeDto::getHolidayEndDate)
                .setHeader("Ta’til tugashi")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(emp -> {
                    if (emp.getHolidayStartDate() != null && emp.getHolidayEndDate() != null) {
                        return emp.getHolidayEndDate().toEpochDay() - emp.getHolidayStartDate().toEpochDay();
                    }
                    return null;
                })
                .setHeader("(kun)")
                .setAutoWidth(true)
                .setSortable(true);

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
                .setSortable(true);

        LitRenderer<EmployeeDto> onHolidayRenderer = LitRenderer.<EmployeeDto>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' " +
                                "style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", emp -> emp.isOnHoliday() ? "check" : "minus")
                .withProperty("color", emp -> emp.isOnHoliday() ? "var(--lumo-success-color)" : "var(--lumo-disabled-text-color)");

        grid.addColumn(onHolidayRenderer)
                .setHeader("Ta’tilda")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.setSizeFull();
    }

    private void refreshGrid() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        List<EmployeeDto> filtered = employees.stream()
                .filter(filters::matches)
                .collect(Collectors.toList());
        grid.setItems(filtered);
    }

    // === Filters ===
    public static class Filters extends Div {

        private final TextField nameField = new TextField("Ism/Familiya/Otasining ismi");
        private final TextField department = new TextField("Bo‘lim");
        private final MultiSelectComboBox<Gender> genders = new MultiSelectComboBox<>("Jinsi");
        private final CheckboxGroup<EmployeeRole> roles = new CheckboxGroup<>("Lavozim");
        private final DatePicker holidayStartFrom = new DatePicker("Ta’til boshlanishi (dan)");
        private final DatePicker holidayEndTo = new DatePicker("Ta’til tugashi (gacha)");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE,
                    LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            nameField.setPlaceholder("Ism, Familiya yoki Otasining ismi");
            department.setPlaceholder("Bo‘lim");
            roles.setItems(EmployeeRole.values());
            genders.setItems(Gender.values());

            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                nameField.clear();
                department.clear();
                roles.clear();
                genders.clear();
                holidayStartFrom.clear();
                holidayEndTo.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            HorizontalLayout actions = new HorizontalLayout(resetBtn, searchBtn);
            actions.setSpacing(true);

            add(nameField, department, roles, genders, createHolidayRangeFilter(), actions);
        }

        private Component createHolidayRangeFilter() {
            FlexLayout dateRange = new FlexLayout(holidayStartFrom, new Text(" – "), holidayEndTo);
            dateRange.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRange.addClassName(LumoUtility.Gap.XSMALL);
            return dateRange;
        }

        public boolean matches(EmployeeDto emp) {
            if (emp == null) return false;
            boolean match = true;

            // Name filter includes first, last, and surName
            if (!nameField.isEmpty()) {
                String filter = nameField.getValue().toLowerCase();
                match &= (emp.getFirstName() != null && emp.getFirstName().toLowerCase().contains(filter))
                        || (emp.getLastName() != null && emp.getLastName().toLowerCase().contains(filter))
                        || (emp.getSurName() != null && emp.getSurName().toLowerCase().contains(filter));
            }

            if (!roles.isEmpty()) {
                match &= emp.getEmployeeRole() != null && roles.getValue().contains(emp.getEmployeeRole());
            }

            if (!genders.isEmpty()) {
                match &= emp.getGender() != null && genders.getValue().contains(emp.getGender());
            }

            if (!department.isEmpty()) {
                match &= emp.getDepartment() != null && emp.getDepartment().toLowerCase().contains(department.getValue().toLowerCase());
            }

            // Holiday range filter
            if (holidayStartFrom.getValue() != null) {
                match &= emp.getHolidayStartDate() != null && !emp.getHolidayStartDate().isBefore(holidayStartFrom.getValue());
            }

            if (holidayEndTo.getValue() != null) {
                match &= emp.getHolidayEndDate() != null && !emp.getHolidayEndDate().isAfter(holidayEndTo.getValue());
            }

            return match;
        }
    }
}
