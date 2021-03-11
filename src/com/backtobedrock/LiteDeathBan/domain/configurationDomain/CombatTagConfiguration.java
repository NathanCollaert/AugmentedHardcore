package com.backtobedrock.LiteDeathBan.domain.configurationDomain;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.runnables.CombatTag.AbstractCombatTag;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CombatTagConfiguration {
    private final boolean playerCombatTag;
    private final boolean monsterCombatTag;
    private final boolean combatTagSelf;
    private final int combatTagTime;
    //    private final NotificationConfiguration notificationConfiguration;
    private final boolean combatTagPlayerKickDeath;
    private final List<String> disableCombatTagInWorlds;

    public CombatTagConfiguration(boolean playerCombatTag,
                                  boolean monsterCombatTag,
                                  boolean combatTagSelf,
                                  int combatTagTime,
//                                  NotificationConfiguration notificationConfiguration,
                                  boolean combatTagPlayerKickDeath,
                                  List<String> disableCombatTagInWorlds) {
        this.playerCombatTag = playerCombatTag;
        this.monsterCombatTag = monsterCombatTag;
        this.combatTagSelf = combatTagSelf;
        this.combatTagTime = combatTagTime;
//        this.notificationConfiguration = notificationConfiguration;
        this.combatTagPlayerKickDeath = combatTagPlayerKickDeath;
        this.disableCombatTagInWorlds = disableCombatTagInWorlds;
    }

    public static CombatTagConfiguration deserialize(ConfigurationSection section) {
        boolean cPlayerCombatTag = section.getBoolean("PlayerCombatTag", true);
        boolean cMonsterCombatTag = section.getBoolean("MonsterCombatTag", true);
        boolean cCombatTagSelf = section.getBoolean("CombatTagSelf", true);
        int cCombatTagTime = ConfigUtils.checkMin("CombatTagTime", section.getInt("CombatTagTime", 15), 1);
//        NotificationConfiguration cNotificationConfiguration = null;
        boolean cCombatTagPlayerKickDeath = section.getBoolean("CombatTagPlayerKickDeath", true);
        List<String> cDisableCombatTagInWorlds = section.getStringList("DisableCombatTagInWorlds");

//        ConfigurationSection notificationSection = section.getConfigurationSection("CombatTagNotification");
//        if (notificationSection != null)
//            cNotificationConfiguration = NotificationConfiguration.deserialize(notificationSection);

        if (cCombatTagTime == -10) {
            return null;
        }

        return new CombatTagConfiguration(cPlayerCombatTag,
                cMonsterCombatTag,
                cCombatTagSelf,
                cCombatTagTime,
//                cNotificationConfiguration,
                cCombatTagPlayerKickDeath,
                cDisableCombatTagInWorlds);
    }

    public boolean isPlayerCombatTag() {
        return playerCombatTag;
    }

    public boolean isMonsterCombatTag() {
        return monsterCombatTag;
    }

    public boolean isCombatTagSelf() {
        return combatTagSelf;
    }

    public int getCombatTagTime() {
        return combatTagTime;
    }

    public List<AbstractCombatTag> getNotificationConfiguration(Player player, PlayerData playerData, Killer tagger) {
        List<AbstractCombatTag> combatTag = new ArrayList<>();
        combatTag.add(new AbstractCombatTag(player, playerData, tagger));
//        if (this.notificationConfiguration != null) {
//            if (this.notificationConfiguration.getChatNotification() != null)
//                combatTag.add(new ChatTag(player, playerData, tagger, this.notificationConfiguration.getChatNotification()));
//            if (this.notificationConfiguration.getTitleNotification() != null)
//                combatTag.add(new TitleTag(player, playerData, tagger, this.notificationConfiguration.getTitleNotification()));
//            if (this.notificationConfiguration.getBossBarNotification() != null)
//                combatTag.add(new BossBarTag(player, playerData, tagger, this.notificationConfiguration.getBossBarNotification()));
//            if (this.notificationConfiguration.getActionBarNotification() != null)
//                combatTag.add(new ActionBarTag(player, playerData, tagger, this.notificationConfiguration.getActionBarNotification()));
//        }

        return combatTag;
    }

    public boolean isCombatTagPlayerKickDeath() {
        return combatTagPlayerKickDeath;
    }

    public List<String> getDisableCombatTagInWorlds() {
        return disableCombatTagInWorlds;
    }
}
