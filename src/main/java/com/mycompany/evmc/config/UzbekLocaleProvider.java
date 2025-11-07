package com.mycompany.evmc.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class UzbekLocaleProvider implements I18NProvider {

    public static final Locale UZBEK_LOCALE = new Locale("uz", "UZ");

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.singletonList(UZBEK_LOCALE);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle("translations", UZBEK_LOCALE);

        if (bundle.containsKey(key)) {
            String value = bundle.getString(key);
            if (params.length > 0) {
                return MessageFormat.format(value, params);
            }
            return value;
        }

        return key; // fallback
    }
}
