package com.mycompany.evmc.views.profile;

import com.mycompany.evmc.dto.VacationDto;
import com.mycompany.evmc.model.AppUser;
import com.mycompany.evmc.model.Employee;
import com.mycompany.evmc.service.AppUserService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;

@PageTitle("Profile")
@Route(value = "/", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "MANAGER"})
public class EmployeeProfileView extends VerticalLayout {

    public EmployeeProfileView(AuthenticationContext authContext,
                               AppUserService appUserService) {

        authContext.getAuthenticatedUser(UserDetails.class)
                .ifPresentOrElse(user -> {
                    String username = user.getUsername();

                    // Use username instead of email
                    AppUser currentUser = appUserService.findByUsername(username);

                    add(new H2("Welcome, " + currentUser.getUsername()));
                    add(new Paragraph("Role: " + currentUser.getRole()));
                    add(new Paragraph("You can manage employees’ vacation records."));

                }, () -> add(new Paragraph("No user logged in")));
    }
}
