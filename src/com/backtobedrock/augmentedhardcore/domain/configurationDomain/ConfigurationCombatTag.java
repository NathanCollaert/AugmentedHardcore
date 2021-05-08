package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigurationCombatTag {
    private final boolean combatTagSelf;
    private final int combatTagTime;
    private final boolean combatTagPlayerKickDeath;
    private final List<String> disableCombatTagInWorlds;
    private boolean playerCombatTag;
    private boolean monsterCombatTag;

    public ConfigurationCombatTag(
            boolean playerCombatTag,
            boolean monsterCombatTag,
            boolean combatTagSelf,
            int combatTagTime,
            boolean combatTagPlayerKickDeath,
            List<String> disableCombatTagInWorlds
    ) {
        this.playerCombatTag = playerCombatTag;
        this.monsterCombatTag = monsterCombatTag;
        this.combatTagSelf = combatTagSelf;
        this.combatTagTime = combatTagTime;
        this.combatTagPlayerKickDeath = combatTagPlayerKickDeath;
        this.disableCombatTagInWorlds = disableCombatTagInWorlds;
    }

    public static ConfigurationCombatTag deserialize(ConfigurationSection section) {
        boolean cPlayerCombatTag = section.getBoolean("PlayerCombatTag", true);
        boolean cMonsterCombatTag = section.getBoolean("MonsterCombatTag", true);
        boolean cCombatTagSelf = section.getBoolean("CombatTagSelf", true);
        int cCombatTagTime = ConfigUtils.checkMinMax("CombatTagTime", section.getInt("CombatTagTime", 15), 1, Integer.MAX_VALUE);
        boolean cCombatTagPlayerKickDeath = section.getBoolean("CombatTagPlayerKickDeath", true);
        List<String> cDisableCombatTagInWorlds = section.getStringList("DisableCombatTagInWorlds");

        if (cCombatTagTime == -10) {
            return null;
        }

        return new ConfigurationCombatTag(
                cPlayerCombatTag,
                cMonsterCombatTag,
                cCombatTagSelf,
                cCombatTagTime,
                cCombatTagPlayerKickDeath,
                cDisableCombatTagInWorlds
        );
    }

    public boolean isPlayerCombatTag() {
        return playerCombatTag;
    }

    public void setPlayerCombatTag(boolean playerCombatTag) {
        this.playerCombatTag = playerCombatTag;
    }

    public boolean isMonsterCombatTag() {
        return monsterCombatTag;
    }

    public void setMonsterCombatTag(boolean monsterCombatTag) {
        this.monsterCombatTag = monsterCombatTag;
    }

    public boolean isCombatTagSelf() {
        return combatTagSelf;
    }

    public boolean isUseCombatTag() {
        return (this.isPlayerCombatTag() || this.isMonsterCombatTag());
    }

    public int getCombatTagTime() {
        return combatTagTime;
    }

    public boolean isCombatTagPlayerKickDeath() {
        return combatTagPlayerKickDeath;
    }

    public List<String> getDisableCombatTagInWorlds() {
        return disableCombatTagInWorlds;
    }
}
