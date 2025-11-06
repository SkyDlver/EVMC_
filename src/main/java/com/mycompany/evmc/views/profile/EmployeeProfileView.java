package com.mycompany.evmc.views.profile;

import com.mycompany.evmc.model.VacationRequest;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.service.VacationRequestService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Profile")
@Route(value = "/", layout = MainLayout.class)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class EmployeeProfileView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final VacationRequestService vacationRequestService;

    private final Grid<VacationRequest> grid = new Grid<>(VacationRequest.class, false);
    private final TextField typeFilter = new TextField("Filter by Type");
    private final ComboBox<String> statusFilter = new ComboBox<>("Filter by Status");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public EmployeeProfileView(AuthenticationContext authContext,
                               EmployeeService employeeService,
                               VacationRequestService vacationRequestService) {
        this.employeeService = employeeService;
        this.vacationRequestService = vacationRequestService;

        setSpacing(true);
        setPadding(true);

        authContext.getAuthenticatedUser(UserDetails.class)
                .ifPresentOrElse(user -> {
                    String username = user.getUsername();
                    add(new H2("Welcome, " + username));

                    Employee currentEmployee = employeeService.findByEmail(username);
                    add(new Paragraph("Your email: " + username));

                    List<VacationRequest> requests = vacationRequestService.getRequestsByEmployee(currentEmployee);

                    if (requests.isEmpty()) {
                        add(new Paragraph("You have no vacation requests."));
                    } else {
                        configureFilters(requests);
                        configureGrid();
                        refreshGrid(requests);

                        add(typeFilter, statusFilter, grid);
                    }

                }, () -> add(new Paragraph("No user logged in")));
    }

    private void configureGrid() {
        grid.removeAllColumns();

        grid.addColumn(r -> r.getType().getDisplayName())
                .setHeader("Type")
                .setSortable(true);

        grid.addColumn(r -> r.getStartTimestamp().format(formatter))
                .setHeader("Start")
                .setSortable(true);

        grid.addColumn(r -> r.getEndTimestamp().format(formatter))
                .setHeader("End")
                .setSortable(true);

        grid.addColumn(VacationRequest::getUnits)
                .setHeader("Units")
                .setSortable(true);

        grid.addColumn(VacationRequest::getUnitType)
                .setHeader("Unit Type")
                .setSortable(true);

        // Color-coded status badges
        grid.addComponentColumn(r -> {
            Span span = new Span(r.getStatus());
            switch (r.getStatus().toLowerCase()) {
                case "pending" -> span.getStyle().set("color", "orange");
                case "approved" -> span.getStyle().set("color", "green");
                case "rejected" -> span.getStyle().set("color", "red");
            }
            return span;
        }).setHeader("Status").setSortable(true);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();
        grid.setHeight("400px");
    }

    private void configureFilters(List<VacationRequest> requests) {
        typeFilter.setPlaceholder("Type");
        typeFilter.addValueChangeListener(e -> refreshGrid(requests));

        statusFilter.setItems(requests.stream()
                .map(VacationRequest::getStatus)
                .distinct()
                .collect(Collectors.toList()));
        statusFilter.addValueChangeListener(e -> refreshGrid(requests));
    }

    private void refreshGrid(List<VacationRequest> requests) {
        List<VacationRequest> filtered = requests.stream()
                .filter(r -> typeFilter.isEmpty() ||
                        r.getType().getDisplayName().toLowerCase().contains(typeFilter.getValue().toLowerCase()))
                .filter(r -> statusFilter.isEmpty() || r.getStatus().equals(statusFilter.getValue()))
                .collect(Collectors.toList());

        grid.setItems(filtered);
    }
}
