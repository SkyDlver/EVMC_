package com.mycompany.evmc.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.i18n.I18NProvider;

import java.util.Locale;

import com.mycompany.evmc.config.UzbekLocaleProvider;

/**
 * Asosiy interfeys (MainLayout) — bu barcha sahifalar uchun umumiy tuzilma.
 * Drawer (yon menyu) va Navbar (yuqori menyu) dan iborat.
 */
@AnonymousAllowed
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private H1 viewTitle;
    private final AuthenticationContext auth;
    private final I18NProvider i18n = new UzbekLocaleProvider();
    private final Locale locale = new Locale("uz", "UZ");

    public MainLayout(AuthenticationContext auth) {
        this.auth = auth;
        setPrimarySection(Section.DRAWER);

        // Dastlabki til sozlamasi
        UI.getCurrent().setLocale(locale);

        // Drawer (yon menyu) va Header (yuqori menyu) qo‘shish
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menyu almashtirish");

        viewTitle = new H1(getTranslation("app.title", "My-App"));
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Theme toggle
        Button themeToggle = new Button(new Icon("vaadin", "moon"));
        themeToggle.setAriaLabel("Toggle dark/light theme");
        themeToggle.addClickListener(e -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) themeList.remove(Lumo.DARK);
            else themeList.add(Lumo.DARK);
        });

        // Language toggle
        Button langToggle = new Button(UI.getCurrent().getLocale().getLanguage().equals("en") ? "EN" : "UZ");
        langToggle.setAriaLabel("Toggle language");
        langToggle.addClickListener(e -> {
            UI ui = UI.getCurrent();
            Locale current = ui.getLocale();
            if ("en".equals(current.getLanguage())) {
                ui.setLocale(new Locale("uz"));
                langToggle.setText("UZ");
            } else {
                ui.setLocale(new Locale("en"));
                langToggle.setText("EN");
            }
            ui.getPage().reload();
        });

        // Logout button
        Button logout = new Button("Chiqish", e -> auth.logout());

        // Put everything in one HorizontalLayout
        HorizontalLayout headerLayout = new HorizontalLayout(toggle, viewTitle, themeToggle, langToggle, logout);
        headerLayout.setWidthFull();
        headerLayout.expand(viewTitle); // makes viewTitle take all space, pushing logout to right
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setPadding(false);
        headerLayout.setSpacing(true); // optional spacing between buttons

        addToNavbar(headerLayout);
    }


    /** Yon menyu tarkibi */
    private void addDrawerContent() {
        Span appName = new Span("Xodimlarning ta’tilni boshqarish tizimi");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        Header header = new Header(appName);
        Scroller scroller = new Scroller(createNavigation());
        Footer footer = createFooter();

        addToDrawer(header, scroller, footer);
    }

    /** Yon menyu navigatsiyasi */
    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (auth.isAuthenticated()) {
            if (auth.hasRole("EMPLOYEE")) {
                nav.addItem(new SideNavItem("Profil", ""));
                nav.addItem(new SideNavItem("Filtrlar", "grid-filters"));
                nav.addItem(new SideNavItem("Xodim ma’lumotlari", "master-detail"));
                nav.addItem(new SideNavItem("Xodimlar ro‘yxati", "employees"));
            }
            if (auth.hasRole("ADMIN")) {
                nav.addItem(new SideNavItem("Profil", ""));
                nav.addItem(new SideNavItem("Xodimlar", "employees"));
                nav.addItem(new SideNavItem("Filtrlar", "grid-filters"));
            }
        } else {
            nav.addItem(new SideNavItem("Kirish", "login"));
        }

        return nav;
    }

    /** Pastki qism (footer) */
    private Footer createFooter() {
        Footer footer = new Footer();
        Span text = new Span("© 2025 EVMC. Barcha huquqlar himoyalangan.");
        text.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        footer.add(text);
        return footer;
    }

    /** Navigatsiyadan keyin sarlavhani yangilash */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
