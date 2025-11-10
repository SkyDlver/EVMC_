package com.mycompany.evmc.views.profile;

import com.mycompany.evmc.model.AppUser;
import com.mycompany.evmc.service.AppUserService;
import com.mycompany.evmc.views.MainLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.userdetails.UserDetails;

@PageTitle("Profil")
@Route(value = "/", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "MANAGER", "VIEWER"})
public class EmployeeProfileView extends VerticalLayout {

    public EmployeeProfileView(AuthenticationContext authContext,
                               AppUserService appUserService) {

        setSizeFull();
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        addClassName("profile-view");

        // === Sarlavha ===
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        headerLayout.setSpacing(true);

        Icon profileIcon = VaadinIcon.USER_CARD.create();
        profileIcon.setSize("40px");
        profileIcon.setColor("var(--lumo-primary-color)");

        H2 title = new H2("EVMC — Xodimlar Ta’tilini Boshqarish Markazi");
        title.getStyle().set("color", "var(--lumo-primary-color)");

        headerLayout.add(profileIcon, title);
        add(headerLayout);

        Paragraph subtitle = new Paragraph("Xodimlarning ta’til jarayonlarini boshqarish uchun zamonaviy platforma.");
        subtitle.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("max-width", "700px")
                .set("text-align", "center");
        add(subtitle);

        // === Ilova haqida ===
        VerticalLayout aboutCard = createInfoCard(
                VaadinIcon.INFO_CIRCLE.create(),
                "EVMC nima?",
                "EVMC xodimlarning ta’til jarayonlarini boshqarishni soddalashtiradi.\n\n" +
                        "Administratorlar va menejerlar xodim ma’lumotlarini ko‘rish, qidirish va tahrirlashlari, " +
                        "ta’til sanalarini kuzatishlari hamda tizim orqali hisobotlarni olishlari mumkin."
        );

        add(aboutCard);

        // === Foydalanuvchi ma’lumotlari ===
        authContext.getAuthenticatedUser(UserDetails.class).ifPresentOrElse(user -> {
            String username = user.getUsername();
            AppUser currentUser = appUserService.findByUsername(username);

            VerticalLayout userCard = createInfoCard(
                    VaadinIcon.USER.create(),
                    "Sizning profilingiz",
                    "👤 Foydalanuvchi nomi: " + currentUser.getUsername() +
                            "\n🔑 Rol: " + currentUser.getRole() +
                            "\n\nSiz xodimlarning ta’til yozuvlarini boshqarish huquqiga egasiz."
            );

            add(userCard);
        }, () -> add(new Paragraph("Foydalanuvchi tizimga kirmagan.")));
    }

    private VerticalLayout createInfoCard(Icon icon, String headerText, String contentText) {
        icon.setSize("30px");
        icon.setColor("var(--lumo-primary-color)");

        H3 header = new H3(headerText);
        header.getStyle().set("margin", "0");

        Paragraph content = new Paragraph(contentText);
        content.getStyle().set("white-space", "pre-line")
                .set("text-align", "center")
                .set("color", "var(--lumo-secondary-text-color)");

        HorizontalLayout headerRow = new HorizontalLayout(icon, header);
        headerRow.setAlignItems(Alignment.CENTER);
        headerRow.setSpacing(true);

        VerticalLayout card = new VerticalLayout(headerRow, content);
        card.addClassName("info-card");
        card.setWidth("80%");
        card.setMaxWidth("800px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("padding", "1.5em")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)")
                .set("margin-top", "1em");

        return card;
    }
}
