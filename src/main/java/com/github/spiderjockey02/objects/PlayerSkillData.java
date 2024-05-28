package com.github.spiderjockey02.objects;


public class PlayerSkillData {
    private int xp;
    private int level;

    public PlayerSkillData(int xp, int level) {
        this.xp = xp;
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int xp) {
        this.xp+= xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
