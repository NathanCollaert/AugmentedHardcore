package com.backtobedrock.augmentedhardcore.configs;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.NotificationConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.runnables.CombatTag.*;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

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

    public String getIPBanMessage() {
        return this.messages.getString("IPBanMessage", "&fSomeone on your IP address died due to %ban_damage_cause% causing you to banned as well.\nYou will be unbanned in &e%ban_time_left_long%&r.");
    }
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

    //<editor-fold desc="Commands" defaultstate="collapsed">
    public String getNoPermissionError() {
        return MessageUtils.replacePlaceholders(this.messages.getString("NoPermissionError", "&cYou have no permission to use this command."), Collections.emptyMap());
    }

    public String getRequireOnlinePlayerError() {
        return MessageUtils.replacePlaceholders(this.messages.getString("RequireOnlinePlayerError", "&cYou will need to log in to use this command."), Collections.emptyMap());
    }

    public String getTargetNotOnlineError(String player) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("TargetNotOnlineError", "&c%player% is currently not online."), placeholders);
    }

    public String getTargetNotPlayedBeforeError(String player) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("TargetNotPlayedBeforeError", "&c%player% has not played on the server before."), placeholders);
    }

    public String getTargetNotBannedByPluginError(String player, String plugin) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("plugin", plugin);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("TargetNotBannedByPluginError", "&c%player% is not death banned by %plugin%."), placeholders);
    }

    public String getCommandUsageHeader() {
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandUsageHeader", "&8&m--------------&6 Command &fUsage &8&m--------------"), Collections.emptyMap());
    }

    public String getCommandUsageFooter() {
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandUsageFooter", "&8&m------------------------------------------"), Collections.emptyMap());
    }

    public String getCommandHelpHeader() {
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandHelpHeader", "&8&m----------&6 Augmented Hardcore &fHelp &8&m----------"), Collections.emptyMap());
    }

    public String getCommandHelpFooter() {
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandHelpFooter", "&8&m-------------------------------------------"), Collections.emptyMap());
    }

    public String getCommandAddLives(String lives, String livesRaw, String livesTotal, String livesTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("lives", lives);
            put("livesraw", livesRaw);
            put("livestotal", livesTotal);
            put("livestotalraw", livesTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddLives", "&aYou've been given &e%lives%&a, you now have &e%livestotal%&a."), placeholders);
    }

    public String getCommandAddLivesSuccess(String player, String lives, String livesRaw, String livesTotal, String livesTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lives", lives);
            put("livesraw", livesRaw);
            put("livestotal", livesTotal);
            put("livestotalraw", livesTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddLivesSuccess", "&aYou successfully gave &e%lives%&a, &e%player% &anow has §e%livestotal%§a."), placeholders);
    }

    public String getCommandAddLifeParts(String lifeparts, String lifepartsRaw, String lifepartsTotal, String lifepartsTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("lifeparts", lifeparts);
            put("lifepartsraw", lifepartsRaw);
            put("lifepartstotal", lifepartsTotal);
            put("lifepartstotalraw", lifepartsTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddLifeParts", "&aYou've been given &e%lifeparts%&a, you now have &e%lifepartstotal%&a."), placeholders);
    }

    public String getCommandAddLifePartsSuccess(String player, String lifeparts, String lifepartsRaw, String lifepartsTotal, String lifepartsTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lifeparts", lifeparts);
            put("lifepartsraw", lifepartsRaw);
            put("lifepartstotal", lifepartsTotal);
            put("lifepartstotalraw", lifepartsTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddLifePartsSuccess", "&aYou successfully gave &e%lives%&a, &e%player% &anow has §e%livesTotal%§a."), placeholders);
    }

    public String getCommandSetLives(String lives, String livesRaw, String livesTotal, String livesTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("lives", lives);
            put("livesraw", livesRaw);
            put("livestotal", livesTotal);
            put("livestotalraw", livesTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetLives", "&aYour &elives &ahave been set to &e%livestotal%&a."), placeholders);
    }

    public String getCommandSetLivesSuccess(String player, String lives, String livesRaw, String livesTotal, String livesTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lives", lives);
            put("livesraw", livesRaw);
            put("livestotal", livesTotal);
            put("livestotalraw", livesTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetLivesSuccess", "&aYou successfully set the &elives &aof &e%player% &ato &e%livestotal%&a."), placeholders);
    }

    public String getCommandSetLifeParts(String lifeparts, String lifepartsRaw, String lifepartsTotal, String lifepartsTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("lifeparts", lifeparts);
            put("lifepartsraw", lifepartsRaw);
            put("lifepartstotal", lifepartsTotal);
            put("lifepartstotalraw", lifepartsTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetLifeParts", "&aYour &elife parts &ahave been set to &e%lifeparts%&a, giving you &e%livestotal% &aand &e%lifepartstotal%&a."), placeholders);
    }

    public String getCommandSetLifePartsSuccess(String player, String lifeParts, String lifePartsRaw, String lifePartsTotal, String lifePartsTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lifeparts", lifeParts);
            put("lifepartsraw", lifePartsRaw);
            put("lifepartstotal", lifePartsTotal);
            put("lifepartstotalraw", lifePartsTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetLifePartsSuccess", "&aYou successfully set the &elife parts &aof &e%player% &ato &e%lifeparts%&a, &e%player% &anow has &e%livestotal% &aand &e%lifepartstotal%&a."), placeholders);
    }

    public String getCommandAddMaxHealth(String maxHealth, String maxHealthRaw, String maxHealthTotal, String maxHealthTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("maxhealth", maxHealth);
            put("maxhealthraw", maxHealthRaw);
            put("maxhealthtotal", maxHealthTotal);
            put("maxhealthtotalraw", maxHealthTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddMaxHealth", "&aYou've been given &e%maxhealth%&a, you now have &e%maxhealthtotal%&a."), placeholders);
    }

    public String getCommandAddMaxHealthSuccess(String player, String maxHealth, String maxHealthRaw, String maxHealthTotal, String maxHealthTotalRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("maxhealth", maxHealth);
            put("maxhealthraw", maxHealthRaw);
            put("maxhealthtotal", maxHealthTotal);
            put("maxhealthtotalraw", maxHealthTotalRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandAddMaxHealthSuccess", "&aYou successfully gave &e%maxhealth%&a, &e%player% &anow has &e%maxhealthtotal%&a."), placeholders);
    }

    public String getCommandSetMaxHealth(String maxHealth, String maxHealthRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("maxhealth", maxHealth);
            put("maxhealthraw", maxHealthRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetMaxHealth", "&aYour &emax health &ahas been set to &e%maxhealthraw%&a."), placeholders);
    }

    public String getCommandSetMaxHealthSuccess(String player, String maxHealth, String maxHealthRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("maxhealth", maxHealth);
            put("maxhealthraw", maxHealthRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandSetMaxHealthSuccess", "&aYou successfully set the &emax health &aof &e%player%&a to &e%maxhealthraw%&a."), placeholders);
    }

    public String getCommandResetSuccess(String player) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandResetSuccess", "&aYou have successfully reset &e%player%&a."), placeholders);
    }

    public String getCommandReloadSuccess(String plugin) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("plugin", plugin);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandReloadSuccess", "&a%plugin% has successfully been reloaded."), placeholders);
    }

    public String getCommandLifePartsLeft(String player, String lifeParts, String lifePartsRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lifeparts", lifeParts);
            put("lifepartsraw", lifePartsRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandLifePartsLeft", "&a%player% currently has &6%lifeparts%&a."), placeholders);
    }

    public String getCommandLivesLeft(String player, String lives, String livesRaw) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("lives", lives);
            put("livesraw", livesRaw);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandLivesLeft", "&a%player% currently has &6%lives%&a."), placeholders);
    }

    public String getCommandNextLifePart(String player, String timeLeft) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("timeleft", timeLeft);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandNextLifePart", "&a%player% will receive a new &elife part in %timeleft%&a."), placeholders);
    }

    public String getCommandNextMaxHealth(String player, String timeLeft) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("timeleft", timeLeft);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandNextMaxHealth", "&a%player% will receive extra &emax health in %timeleft%&a."), placeholders);
    }

    public String getCommandNextRevive(String player, String timeLeft) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
            put("timeleft", timeLeft);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandNextRevive", "&a%player% will be able to &erevive in %timeleft%&a."), placeholders);
    }

    public String getCommandUndeathBan(String player) {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("player", player);
        }};
        return MessageUtils.replacePlaceholders(this.messages.getString("CommandUndeathBan", "&a%player% has successfully been unbanned from a death ban."), placeholders);
    }
    //</editor-fold>
}
