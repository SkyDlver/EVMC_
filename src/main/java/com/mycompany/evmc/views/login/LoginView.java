package com.mycompany.evmc.views.login;

import com.mycompany.evmc.security.SecurityUtils;
import com.mycompany.evmc.views.gridwithfilters.GridwithFiltersView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;

@Route
@PageTitle("Login | EVMC")
@CssImport("./themes/evmc/views/login-view.css")
public class LoginView extends LoginOverlay
        implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        addClassName("login-view");


        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Welcome to EVMC");
        i18n.getHeader().setDescription("""
                Sign in to continue\s
                nafisa.abdullayeva@evmc.uz + 1234
                gary.baker@ji.cf + hashed123""");
        // Customize i18n texts
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setTitle("Sign in");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Login");
        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setAction("login");

//        // Forgot password notification
//        addForgotPasswordListener(event -> {
//            Notification forgotMsg = new Notification(
//                    "Forgot password is not implemented yet",
//                    3000,
//                    Notification.Position.BOTTOM_START
//            );
//            forgotMsg.open();
//        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            event.forwardTo(GridwithFiltersView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setError(
                event.getLocation().getQueryParameters().getParameters().containsKey(
                        "error"));
    }
}
