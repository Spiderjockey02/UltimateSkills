package com.github.spiderjockey02.objects;

import com.github.spiderjockey02.enums.SkillType;
import java.util.*;

public class playerData {
    public UUID playerId;
    public Map<SkillType, PlayerSkillData> skills = new HashMap<>();

    public playerData(UUID playerId) {
        this.playerId = playerId;
        this.skills.put(SkillType.COMBAT, new PlayerSkillData(0,0));
        this.skills.put(SkillType.FARMING, new PlayerSkillData(0,0));
        this.skills.put(SkillType.FISHING, new PlayerSkillData(0,0));
        this.skills.put(SkillType.LUMBERING, new PlayerSkillData(0,0));
        this.skills.put(SkillType.MINING, new PlayerSkillData(0,0));
    }
}
