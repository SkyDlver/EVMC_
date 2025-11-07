package com.mycompany.evmc.views.profile;

import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.service.EmployeeService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

@PageTitle("Profile")
@Route(value = "/", layout = MainLayout.class)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class EmployeeProfileView extends VerticalLayout {
    public EmployeeProfileView(AuthenticationContext authContext,
                               EmployeeService employeeService) {

        authContext.getAuthenticatedUser(UserDetails.class)
                .ifPresentOrElse(user -> {
                    String username = user.getUsername();
                    add(new H2("Welcome, " + username));

                    Employee currentEmployee = employeeService.findByEmail(username);
                    add(new Paragraph("Your email: " + username));

                }, () -> add(new Paragraph("No user logged in")));
    }
}
