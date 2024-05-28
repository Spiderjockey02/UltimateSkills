package com.github.spiderjockey02.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SkillType {
    MINING(),
    FARMING(),
    COMBAT(),
    FISHING(),
    LUMBERING();

    private final String name;

    SkillType() {
        this.name = toString();
    }

    public String getName() {
        return this.name;
    }

    public String getCapitalise() {
        String input = this.name.toLowerCase(); // Convert the entire string to lowercase
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static List<String> getSkills() {
        return new ArrayList<String>(){{
            Arrays.stream(SkillType.values()).forEach(skillType -> {
                add(skillType.getName());
            });
        }};
    }
}

