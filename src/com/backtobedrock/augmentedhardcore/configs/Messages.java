package com.backtobedrock.augmentedhardcore.configs;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.NotificationConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.runnables.CombatTag.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    private final AugmentedHardcore plugin;
    private final FileConfiguration messages;

    //cache
    private NotificationConfiguration combatTagNotifications;

    public Messages(File messagesFile) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    //<editor-fold desc="Ban" defaultstate="collapsed">
    public String getEnvironmentBanMessage() {
        return this.messages.getString("EnvironmentBanMessage", "&c&lYou've Died\n\n&rYou %damage_cause_message_random% causing you to be banned.\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getEnvironmentWhileInCombatBanMessage() {
        return this.messages.getString("EnvironmentWhileInCombatBanMessage", "&c&lYou've Died\n\n&rYou %damage_cause_message_random% whilst trying to escape &b%in_combat_with%&r causing you to be banned.\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getEntityBanMessage() {
        return this.messages.getString("EntityBanMessage", "&c&lYou've Died\n\n&rYou %damage_cause_message_random% by &b%killer%&r causing you to be banned.\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getEntityWhileInCombatBanMessage() {
        return this.messages.getString("EntityWhileInCombatBanMessage", "&c&lYou've Died\n\n&rYou %damage_cause_message_random% by &b%killer%&r whilst trying to escape &b%in_combat_with%&r causing you to be banned.\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getReviveBanMessage() {
        return this.messages.getString("ReviveBanMessage", "&c&lYou've Died\n\n&rYou have successfully revived &b%killer%&r, but at what cost?\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getReviveWhileInCombatBanMessage() {
        return this.messages.getString("ReviveWhileInCombatBanMessage", "&c&lYou've Died\n\n&rYou have successfully revived &b%killer%&r whilst trying to escape &b%in_combat_with%&r, but at what cost?\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }

    public String getCombatLogBanMessage() {
        return this.messages.getString("CombatLogBanMessage", "&c&lYou've Died\n\n&rYou have logged out while in combat with &b%combat_tagger%&r causing you to die and be banned.\nYou will be unbanned in &e%long_ban_time_left%&r.");
    }
    //</editor-fold>

    //<editor-fold desc="0" defaultstate="collapsed">
    //</editor-fold>

    //<editor-fold desc="Combat Tag" defaultstate="collapsed">
    private NotificationConfiguration getCombatTagNotifications() {
        if (this.combatTagNotifications == null) {
            ConfigurationSection notificationSection = messages.getConfigurationSection("CombatTagNotifications");
            if (notificationSection != null) {
                this.combatTagNotifications = NotificationConfiguration.deserialize(notificationSection);
            }
        }
        return this.combatTagNotifications;
    }

    public List<AbstractCombatTag> getCombatTagNotificationsConfiguration(Player player, PlayerData playerData, Killer tagger) {
        List<AbstractCombatTag> combatTag = new ArrayList<>();
        if (this.getCombatTagNotifications() != null) {
            if (this.getCombatTagNotifications().getChatNotification() != null)
                combatTag.add(new ChatTag(player, playerData, tagger, this.getCombatTagNotifications().getChatNotification()));
            if (this.getCombatTagNotifications().getTitleNotification() != null)
                combatTag.add(new TitleTag(player, playerData, tagger, this.getCombatTagNotifications().getTitleNotification()));
            if (this.getCombatTagNotifications().getBossBarNotification() != null)
                combatTag.add(new BossBarTag(player, playerData, tagger, this.getCombatTagNotifications().getBossBarNotification()));
            if (this.getCombatTagNotifications().getActionBarNotification() != null)
                combatTag.add(new ActionBarTag(player, playerData, tagger, this.getCombatTagNotifications().getActionBarNotification()));
        }
        if (combatTag.isEmpty()) {
            combatTag.add(new AbstractCombatTag(player, playerData, tagger));
        }

        return combatTag;
    }
    //</editor-fold>

    //<editor-fold desc="2" defaultstate="collapsed">
    //</editor-fold>
}
