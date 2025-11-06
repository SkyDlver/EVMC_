package com.mycompany.evmc.views.profile;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;                      // correct import
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.mycompany.evmc.views.MainLayout;
import org.springframework.security.core.userdetails.UserDetails;     // or your custom class

@PageTitle("Profile")
@Route(value = "/", layout = MainLayout.class)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class EmployeeProfileView extends VerticalLayout {


    public EmployeeProfileView(AuthenticationContext authContext) {
        setSpacing(false);
        setPadding(true);

        authContext.getAuthenticatedUser(UserDetails.class)
                .ifPresentOrElse(user -> {
                    String username = user.getUsername();   // typically UserDetails has getUsername()
                    add(new H2("Welcome, " + username));
                    add(new Paragraph("Your email: " + username));  // Or replace with user.getEmail(), etc.
                }, () -> {
                    add(new Paragraph("No user logged in"));
                });
    }
}
