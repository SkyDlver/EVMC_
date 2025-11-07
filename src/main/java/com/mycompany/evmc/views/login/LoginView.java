package com.mycompany.evmc.views.login;

import com.mycompany.evmc.security.SecurityUtils;
import com.mycompany.evmc.views.gridwithfilters.GridwithFiltersView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;

@Route
@PageTitle("Kirish | EVMC")
@CssImport("./themes/evmc/views/login-view.css")
public class LoginView extends LoginOverlay
        implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        addClassName("login-view");

        // I18N (O‘zbek tiliga tarjima)
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("EVMC tizimiga xush kelibsiz");
        i18n.getHeader().setDescription("""
                Davom etish uchun tizimga kiring
                Misol: nafisa.abdullayeva@evmc.uz / 1234
                yoki gary.baker@ji.cf / hashed123
                """);

        // Forma matnlari
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setTitle("Tizimga kirish");
        i18n.getForm().setUsername("Elektron pochta");
        i18n.getForm().setPassword("Parol");
        i18n.getForm().setSubmit("Kirish");

        // Xatolik va holat xabarlari
        i18n.setErrorMessage(new LoginI18n.ErrorMessage());
        i18n.getErrorMessage().setTitle("Xatolik!");
        i18n.getErrorMessage().setMessage("Login yoki parol noto‘g‘ri. Qaytadan urinib ko‘ring.");

        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setAction("login");
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
