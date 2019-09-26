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
        File messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(messagesFile);
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

    public String getOnPlayerRespawn(String playername, int livesLeft, int maxLives) {
        String message = this.messages.get("OnPlayerRespawn");
        return this.playernamePH(
                this.livesLeftPH(
                        this.maxLivesPH(message, maxLives),
                        livesLeft),
                playername);
    }

    public String getOnLivesLeftInTabMenu(String playername, int livesLeft, int maxLives) {
        String message = this.messages.get("OnLivesLeftInTabMenu");
        return this.playernamePH(
                this.livesLeftPH(
                        this.maxLivesPH(message, maxLives),
                        livesLeft),
                playername);
    }

    public String getOnExtraPartBroadcast(String playername, int totalParts, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnExtraPartBroadcast");
        return this.playernamePH(
                this.totalPartsPH(
                        this.partsReceivedPH(
                                this.partsNeededForLifePH(message, partsNeededForLife),
                                partsReceived),
                        totalParts),
                playername);
    }

    public String getOnExtraLifeBroadcast(String playername, int livesReceived, int totalLives, int maxLives, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnExtraLifeBroadcast");
        return this.playernamePH(
                this.livesReceivedPH(
                        this.totalLivesPH(
                                this.maxLivesPH(
                                        this.partsReceivedPH(
                                                this.partsNeededForLifePH(message, partsNeededForLife),
                                                partsReceived),
                                        maxLives),
                                totalLives),
                        livesReceived),
                playername);
    }

    public String getOnPartsLostCauseDeath(String playername, int partsNeededForLife) {
        String message = this.messages.get("OnPartsLostCauseDeath");
        return this.playernamePH(
                this.partsNeededForLifePH(message, partsNeededForLife),
                playername);
    }

    public String getOnMaxLivesBroadcast(String playername, int livesReceived, int totalLives, int maxLives, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnMaxLivesBroadcast");
        return this.playernamePH(
                this.livesReceivedPH(
                        this.totalLivesPH(
                                this.maxLivesPH(
                                        this.partsReceivedPH(
                                                this.partsNeededForLifePH(message, partsNeededForLife),
                                                partsReceived),
                                        maxLives),
                                totalLives),
                        livesReceived),
                playername);
    }

    public String getOnExtraLife(String playername, int livesReceived, int totalLives, int maxLives, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnExtraLife");
        return this.playernamePH(
                this.livesReceivedPH(
                        this.totalLivesPH(
                                this.maxLivesPH(
                                        this.partsReceivedPH(
                                                this.partsNeededForLifePH(message, partsNeededForLife),
                                                partsReceived),
                                        maxLives),
                                totalLives),
                        livesReceived),
                playername);
    }

    public String getOnMaxLives(String playername, int livesReceived, int totalLives, int maxLives, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnMaxLives");
        return this.playernamePH(
                this.livesReceivedPH(
                        this.totalLivesPH(
                                this.maxLivesPH(
                                        this.partsReceivedPH(
                                                this.partsNeededForLifePH(message, partsNeededForLife),
                                                partsReceived),
                                        maxLives),
                                totalLives),
                        livesReceived),
                playername);
    }

    public String getOnExtraPart(String playername, int totalParts, int partsReceived, int partsNeededForLife) {
        String message = this.messages.get("OnExtraPart");
        return this.playernamePH(
                this.totalPartsPH(
                        this.partsReceivedPH(
                                this.partsNeededForLifePH(message, partsNeededForLife),
                                partsReceived),
                        totalParts),
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
            convertedMessage = convertedMessage.replaceAll("%bantime_in_minutes%", String.format("%d", banTimeInMinutes));
            convertedMessage = convertedMessage.replaceAll("%bantime_in_hours%", String.format("%.2f", (double) banTimeInMinutes / 60));
            convertedMessage = convertedMessage.replaceAll("%bantime_in_days%", String.format("%.2f", (double) banTimeInMinutes / 60 / 24));
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

    private String maxLivesPH(String message, int maxLives) {
        return message.replace("%max_lives%", Integer.toString(maxLives));
    }

    private String partsReceivedPH(String message, int partsReceived) {
        return message.replace("%parts_received%", Integer.toString(partsReceived));
    }

    private String livesReceivedPH(String message, int livesReceived) {
        return message.replace("%lives_received%", Integer.toString(livesReceived));
    }

    private String totalLivesPH(String message, int totalLives) {
        return message.replace("%total_lives%", Integer.toString(totalLives));
    }

    private String partsNeededForLifePH(String message, int partsNeededForLife) {
        return message.replace("%parts_needed_for_life%", Integer.toString(partsNeededForLife));
    }

    private String totalPartsPH(String message, int totalParts) {
        return message.replace("%total_parts%", Integer.toString(totalParts));
    }
}
