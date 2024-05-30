package com.github.spiderjockey02.objects;

import java.util.List;

public class LevelData {
    private final int xpNeeded;
    private final List<String> lore;
    private final List<String> commands;

    public LevelData(int xpNeeded, List<String> lore, List<String> commands) {
        this.xpNeeded = xpNeeded;
        this.lore = lore;
        this.commands = commands;
    }

    public int getXpNeeded() {
        return xpNeeded;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return "xpNeeded: " + xpNeeded + ", lore: " + lore + ", commands: " + commands;
    }
}
