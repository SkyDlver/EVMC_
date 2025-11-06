package com.mycompany.evmc.views.gridwithfilters;

import com.mycompany.evmc.dto.EmployeeDto;
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
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Employees Directory")
@Route(value = "/grid-filters", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.FILTER_SOLID)
@Uses(Icon.class)
@RolesAllowed({"EMPLOYEE", "ADMIN", "HR", "MANAGER"})
public class GridwithFiltersView extends Div {

    private final EmployeeService employeeService;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private final Filters filters;

    public GridwithFiltersView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setSizeFull();
        addClassNames("gridwith-filters-view");

        filters = new Filters(this::refreshGrid);
        populateTeams();

        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        configureGrid();
        refreshGrid();
    }

    private void populateTeams() {
        List<String> teams = employeeService.getAllEmployees().stream()
                .map(EmployeeDto::getTeam)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .collect(Collectors.toList());
        filters.teams.setItems(teams);
    }

    private HorizontalLayout createMobileFilters() {
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    private void configureGrid() {
        grid.addColumn(EmployeeDto::getFirstName).setHeader("First Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getLastName).setHeader("Last Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getTeam).setHeader("Team").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getRole).setHeader("Role").setAutoWidth(true).setSortable(true);
        grid.addColumn(EmployeeDto::getHiredAt).setHeader("Hired At").setAutoWidth(true).setSortable(true);

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

        private final TextField name = new TextField("Name");
        private final TextField email = new TextField("Email");
        private final MultiSelectComboBox<String> teams = new MultiSelectComboBox<>("Team");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");
        private final DatePicker hiredFrom = new DatePicker("Hired From");
        private final DatePicker hiredTo = new DatePicker("Hired To");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE,
                    LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            name.setPlaceholder("First or Last name");
            email.setPlaceholder("Email");
            roles.setItems("EMPLOYEE", "ADMIN", "HR", "MANAGER");
            teams.setPlaceholder("Select teams");

            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                email.clear();
                roles.clear();
                teams.clear();
                hiredFrom.clear();
                hiredTo.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, email, teams, roles, createDateRangeFilter(), actions);
        }

        private Component createDateRangeFilter() {
            hiredFrom.setPlaceholder("From");
            hiredTo.setPlaceholder("To");

            FlexLayout dateRange = new FlexLayout(hiredFrom, new Text(" – "), hiredTo);
            dateRange.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRange.addClassName(LumoUtility.Gap.XSMALL);

            return dateRange;
        }

        public boolean matches(EmployeeDto emp) {
            if (emp == null) return false;

            boolean match = true;
            if (!name.isEmpty()) {
                String filter = name.getValue().toLowerCase();
                match &= (emp.getFirstName() != null && emp.getFirstName().toLowerCase().contains(filter))
                        || (emp.getLastName() != null && emp.getLastName().toLowerCase().contains(filter));
            }
            if (!email.isEmpty()) {
                match &= emp.getEmail() != null && emp.getEmail().toLowerCase().contains(email.getValue().toLowerCase());
            }
            if (!roles.isEmpty()) {
                match &= emp.getRole() != null && roles.getValue().contains(emp.getRole().toUpperCase());
            }
            if (!teams.isEmpty()) {
                match &= emp.getTeam() != null && teams.getValue().contains(emp.getTeam());
            }
            if (hiredFrom.getValue() != null) {
                match &= emp.getHiredAt() != null && !emp.getHiredAt().isBefore(hiredFrom.getValue());
            }
            if (hiredTo.getValue() != null) {
                match &= emp.getHiredAt() != null && !emp.getHiredAt().isAfter(hiredTo.getValue());
            }
            return match;
        }
    }
}
