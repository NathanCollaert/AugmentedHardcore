package com.backtobedrock.LiteDeathBan;

import java.io.File;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LiteDeathBanMessages {

    private final FileConfiguration config;
    private final LiteDeathBan plugin;

    private TreeMap<String, String> messages = new TreeMap<>();

    public LiteDeathBanMessages(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder() + "/messages.yml"));
        this.config.getValues(true).entrySet().forEach((e) -> {
            this.messages.put(e.getKey(), e.getValue().toString().replaceAll("&", "§"));
        });
    }

    public String getOnPlayerDeathBan(String playername, int banTimeInMinutes, String banExpireDate, String dateOfLastBan, int totalBans) {
        String message = this.messages.get("OnPlayerDeathBan");
        return this.playernamePH(
                this.banTimePH(
                        this.banExpireDatePH(
                                this.dateOfLastBanPH(
                                        this.totalBansPH(message, totalBans),
                                        dateOfLastBan),
                                banExpireDate),
                        banTimeInMinutes),
                playername);
    }

    public String getOnCombatTaggedChat(String playername, String taggedBy, int tagTime) {
        String message = this.messages.get("OnCombatTaggedChat");
        return this.playernamePH(
                this.taggedByPH(
                        this.tagTimePH(message,
                                tagTime),
                        taggedBy),
                playername);
    }

    public String getOnCombatTaggedChatEnd(String playername, String taggedBy, int tagTime) {
        String message = this.messages.get("OnCombatTaggedChatEnd");
        return this.playernamePH(
                this.taggedByPH(
                        this.tagTimePH(message,
                                tagTime),
                        taggedBy),
                playername);
    }

    public String getOnCombatTaggedBossBar(String playername, String taggedBy, int tagTime) {
        String message = this.messages.get("OnCombatTaggedBossBar");
        return this.playernamePH(
                this.taggedByPH(
                        this.tagTimePH(message,
                                tagTime),
                        taggedBy),
                playername);
    }

    public String getOnPlayerRespawn(String playername, int livesLeft) {
        String message = this.messages.get("OnPlayerRespawn");
        return this.playernamePH(
                this.livesLeftPH(message,
                        livesLeft),
                playername);
    }

    private String playernamePH(String message, String playername) {
        return message.replace("%playername%", playername);
    }

    private String taggedByPH(String message, String killedBy) {
        return message.replace("%tagged_by%", killedBy);
    }

    private String banTimePH(String message, int banTimeInMinutes) {
        String convertedMessage = message;
        Pattern pattern = Pattern.compile("\\%(\\S*?)\\%");
        Matcher m = pattern.matcher(convertedMessage);
        while (m.find()) {
            convertedMessage = convertedMessage.replaceAll("%bantime_in_minutes%", Integer.toString(banTimeInMinutes));
            convertedMessage = convertedMessage.replaceAll("%bantime_in_hours%", Double.toString((double) banTimeInMinutes / 60));
            convertedMessage = convertedMessage.replaceAll("%bantime_in_days%", Double.toString((double) banTimeInMinutes / 60 / 24));
        }
        return convertedMessage;
    }

    private String banExpireDatePH(String message, String banExpireDate) {
        return message.replace("%ban_expire_date%", banExpireDate);
    }

    private String dateOfLastBanPH(String message, String dateOfLastBan) {
        return message.replace("%date_of_last_ban%", dateOfLastBan);
    }

    private String totalBansPH(String message, int totalBans) {
        return message.replace("%total_bans%", Integer.toString(totalBans));
    }

    private String tagTimePH(String message, int tagTime) {
        return message.replace("%tag_time%", Integer.toString(tagTime));
    }

    private String livesLeftPH(String message, int livesLeft) {
        return message.replace("%lives_left%", Integer.toString(livesLeft));
    }
}
