package com.github.spiderjockey02.utils;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public final class StringUtils {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> strings) {
        return strings.stream().map(StringUtils::color).collect(Collectors.toList());
    }

    public static String getProgressBar(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        // Just make sure current is not more than max
        float percent;
        if (current > max) {
            percent = 1F;
        } else {
            percent = (float) current / max;
        }

        int progressBars = (int) (totalBars * percent);
        return color(Strings.repeat(completedColor + symbol, progressBars)
                + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars));
    }
}
