package com.mycompany.evmc.views.manager;

import com.mycompany.evmc.model.VacationRequest;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.service.VacationRequestService;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Manager - Vacation Requests")
@Route(value = "/manager-vacations", layout = MainLayout.class)
@RolesAllowed({"MANAGER", "ADMIN"})
public class ManagerVacationView extends VerticalLayout {

    private final VacationRequestService vacationRequestService;
    private final EmployeeService employeeService;

    private final Grid<VacationRequest> grid = new Grid<>(VacationRequest.class, false);

    private final TextField employeeFilter = new TextField("Employee Name or Email");
    private final TextField typeFilter = new TextField("Type");
    private final ComboBox<String> statusFilter = new ComboBox<>("Status");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ManagerVacationView(VacationRequestService vacationRequestService,
                               EmployeeService employeeService) {
        this.vacationRequestService = vacationRequestService;
        this.employeeService = employeeService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("All Vacation Requests"));

        List<VacationRequest> requests = vacationRequestService.getAllRequests();

        configureFilters(requests);
        configureGrid();
        refreshGrid(requests);

        add(employeeFilter, typeFilter, statusFilter, grid);
    }

    private void configureFilters(List<VacationRequest> requests) {
        employeeFilter.setPlaceholder("Filter by employee name/email");
        employeeFilter.addValueChangeListener(e -> refreshGrid(requests));

        typeFilter.setPlaceholder("Filter by type");
        typeFilter.addValueChangeListener(e -> refreshGrid(requests));

        statusFilter.setItems(requests.stream()
                .map(VacationRequest::getStatus)
                .distinct()
                .collect(Collectors.toList()));
        statusFilter.addValueChangeListener(e -> refreshGrid(requests));
    }

    private void configureGrid() {
        grid.removeAllColumns();

        grid.addColumn(r -> r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName())
                .setHeader("Employee")
                .setSortable(true);

        grid.addColumn(r -> r.getEmployee().getEmail())
                .setHeader("Email")
                .setSortable(true);

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

        // Status column with color
        grid.addColumn(r -> r.getStatus())
                .setHeader("Status")
                .setSortable(true);

        // Actions column
        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions");

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();
        grid.setHeight("600px");
    }

    private HorizontalLayout createActionButtons(VacationRequest request) {
        HorizontalLayout layout = new HorizontalLayout();

        if ("pending".equalsIgnoreCase(request.getStatus())) {
            Button approve = new Button("Approve", e -> openDecisionDialog(request, "approved"));
            Button reject = new Button("Reject", e -> openDecisionDialog(request, "rejected"));
            approve.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            reject.addThemeVariants(ButtonVariant.LUMO_ERROR);
            layout.add(approve, reject);
        } else {
            layout.add(new Span("-"));
        }

        return layout;
    }

    private void openDecisionDialog(VacationRequest request, String status) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span("Add a comment (optional) for " + status + ":"));

        TextArea commentArea = new TextArea();
        commentArea.setWidthFull();
        layout.add(commentArea);

        Button confirm = new Button("Confirm", e -> {
            // get current manager from security context
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee approver = employeeService.findByEmail(username);

            vacationRequestService.updateStatus(request.getId(), status, approver, commentArea.getValue());
            refreshGrid(vacationRequestService.getAllRequests());
            dialog.close();
        });

        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(confirm);
        dialog.add(layout);
        dialog.open();
    }

    private void refreshGrid(List<VacationRequest> requests) {
        List<VacationRequest> filtered = requests.stream()
                .filter(r -> employeeFilter.isEmpty() ||
                        (r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName())
                                .toLowerCase().contains(employeeFilter.getValue().toLowerCase())
                        || r.getEmployee().getEmail().toLowerCase().contains(employeeFilter.getValue().toLowerCase()))
                .filter(r -> typeFilter.isEmpty() ||
                        r.getType().getDisplayName().toLowerCase().contains(typeFilter.getValue().toLowerCase()))
                .filter(r -> statusFilter.isEmpty() || r.getStatus().equals(statusFilter.getValue()))
                .collect(Collectors.toList());

        grid.setItems(filtered);
    }
}
