package com.mycompany.evmc.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */

public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private H1 viewTitle;
    private final AuthenticationContext auth;

    public MainLayout(AuthenticationContext auth) {
        this.auth = auth;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Button logout = new Button("Logout", e -> auth.logout());
        logout.addClassNames(LumoUtility.Margin.Left.AUTO);

        addToNavbar(true, toggle, viewTitle, logout);
    }


    private void addDrawerContent() {
        Span appName = new Span("EVMC");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (auth.isAuthenticated()) {
            if (auth.hasRole("EMPLOYEE")) {
                nav.addItem(new SideNavItem("Profile", ""));
                nav.addItem(new SideNavItem("Grid Filters", "grid-filters"));
            }
            if (auth.hasRole("MANAGER")) {
                nav.addItem(new SideNavItem("Master Detail", "master-detail"));
                nav.addItem(new SideNavItem("Grid Filters", "grid-filters"));
            }
            if (auth.hasRole("ADMIN")) {
                nav.addItem(new SideNavItem("Profile", ""));
                nav.addItem(new SideNavItem("Employees", "employees"));
                nav.addItem(new SideNavItem("Grid Filters", "grid-filters"));
            }
        }

        return nav;
    }



    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
