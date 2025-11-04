package com.mycompany.evmc.views;

import com.mycompany.evmc.dto.LoginRequest;
import com.mycompany.evmc.dto.LoginResponse;
import com.mycompany.evmc.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
        setAlignItems(Alignment.CENTER);

        TextField email = new TextField("Email");
        PasswordField password = new PasswordField("Password");
        Button loginBtn = new Button("Login");



        add(new H1("Employee Portal"), email, password, loginBtn);
    }
}
