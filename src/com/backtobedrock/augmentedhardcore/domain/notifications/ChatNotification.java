package com.backtobedrock.augmentedhardcore.domain.notifications;

import org.bukkit.configuration.ConfigurationSection;

public class ChatNotification {
    private final String textStart;
    private final String textEnd;

    public ChatNotification(String textStart, String textEnd) {
        this.textStart = textStart;
        this.textEnd = textEnd;
    }

    public static ChatNotification deserialize(ConfigurationSection section) {
        String cTextStart = section.getString("TextStart", "");
        String cTextEnd = section.getString("TextEnd", "");
        return new ChatNotification(cTextStart, cTextEnd);
    }

    public String getTextStart() {
        return textStart;
    }

    public String getTextEnd() {
        return textEnd;
    }
}
