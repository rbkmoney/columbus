package com.rbkmoney.columbus.model;

import java.util.HashMap;
import java.util.Map;

public enum Lang {
    RU("ru"),
    DE("de"),
    ENG("en"),
    ESP("es"),
    FR("fr"),
    JAP("ja"),
    PT("pt-BR"),
    CH("zh-CN"),
    UNKNOWN("unknown");

    // Reverse-lookup map for getting a Lang from an abbreviation
    private static final Map<String, Lang> lookup = new HashMap<String, Lang>();

    static {
        for (Lang l : Lang.values()) {
            lookup.put(l.getValue(), l);
        }
    }

    private final String abbreviation;

    Lang(String lang) {
        this.abbreviation = lang;
    }

    public String getValue() {
        return abbreviation;
    }

    public static Lang getByAbbreviation(String abbreviation) {
        if (lookup.containsKey(abbreviation)) {
            return lookup.get(abbreviation);
        } else {
            return UNKNOWN;
        }
    }
}
