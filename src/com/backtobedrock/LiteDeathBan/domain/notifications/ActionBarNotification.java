package com.backtobedrock.LiteDeathBan.domain.notifications;

import org.bukkit.configuration.ConfigurationSection;

public class ActionBarNotification {
    private final String textStart;
    private final String textEnd;

    public ActionBarNotification(String textStart, String textEnd) {
        this.textStart = textStart;
        this.textEnd = textEnd;
    }

    public static ActionBarNotification deserialize(ConfigurationSection section) {
        String cTextStart = section.getString("TextStart", "");
        String cTextEnd = section.getString("TextEnd", "");
        return new ActionBarNotification(cTextStart, cTextEnd);
    }

    public String getTextEnd() {
        return textEnd;
    }

    public String getTextStart() {
        return textStart;
    }
}
