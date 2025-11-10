package com.mycompany.evmc.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        // Drawer toggle
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menyu almashtirish");

        // View title
        viewTitle = new H1(getTranslation("app.title", "My-App"));
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Theme toggle button
        Button themeToggle = new Button(new Icon("vaadin", "moon"));
        themeToggle.setAriaLabel("Toggle dark/light theme");

        // Apply current theme from localStorage on page load
        UI.getCurrent().getPage().executeJs(
                "if(localStorage.getItem('theme') === 'dark') { document.body.setAttribute('theme', 'dark'); }"
        );

        themeToggle.addClickListener(e -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                UI.getCurrent().getPage().executeJs("localStorage.setItem('theme', 'light');");
            } else {
                themeList.add(Lumo.DARK);
                UI.getCurrent().getPage().executeJs("localStorage.setItem('theme', 'dark');");
            }
        });

        // Language toggle button
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

        // Cog button with context menu
        Button cogButton = new Button(new Icon(VaadinIcon.COG));
        cogButton.setAriaLabel("Settings menu");
        cogButton.getStyle().set("min-width", "auto");
        cogButton.getStyle().set("padding", "0.25em");
        cogButton.getStyle().set("margin-right", "0.5em");

        ContextMenu cogMenu = new ContextMenu(cogButton);
        cogMenu.setOpenOnClick(true);
        cogMenu.addItem("Profil", e -> UI.getCurrent().navigate("/"));
        cogMenu.addItem("Chiqish", e -> auth.logout());

        // Assemble header layout
        HorizontalLayout headerLayout = new HorizontalLayout(toggle, viewTitle, themeToggle, langToggle, cogButton);
        headerLayout.setWidthFull();
        headerLayout.expand(viewTitle); // title takes remaining space
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setPadding(false);
        headerLayout.setSpacing(true);

        addToNavbar(headerLayout);
    }



    /** Yon menyu tarkibi */
    private void addDrawerContent() {
        // Create a vertical layout to hold image + text
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Add image from resources
        Image gerbImage = new Image("themes/evmc/images/gerb.png", "Gerb");
        gerbImage.setWidth("128px");
        gerbImage.setHeight("128px");
        gerbImage.getStyle().set("margin-top", "5px");

        // App name
        Span appName = new Span("Xodimlarning ta’tilni boshqarish tizimi");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.MEDIUM);

        headerLayout.add(gerbImage, appName);

        Header header = new Header(headerLayout);
        Scroller scroller = new Scroller(createNavigation());
        Footer footer = createFooter();

        addToDrawer(header, scroller, footer);
    }



        /** Yon menyu navigatsiyasi */
        private SideNav createNavigation() {
            SideNav nav = new SideNav();

            if (auth.isAuthenticated()) {

                // Common for all authenticated users
                nav.addItem(new SideNavItem("Profil", "/"));

                // Only VIEWER, ADMIN, and MANAGER can see filters
                if (auth.hasRole("VIEWER") || auth.hasRole("ADMIN") || auth.hasRole("MANAGER")) {
                    nav.addItem(new SideNavItem("Filtrlar", "grid-filters"));
                }


                // Only ADMIN or MANAGER can see Xodimlar
                if (auth.hasRole("ADMIN") || auth.hasRole("MANAGER")) {
                    nav.addItem(new SideNavItem("Xodimlar", "employees"));
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
