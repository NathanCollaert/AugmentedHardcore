package com.backtobedrock.augmentedhardcore.domain.notifications;

import org.bukkit.configuration.ConfigurationSection;

public class TitleNotification {
    private final String titleStart;
    private final String subTitleStart;
    private final String titleEnd;
    private final String subTitleEnd;

    public TitleNotification(String titleStart, String subTitleStart, String titleEnd, String subTitleEnd) {
        this.titleStart = titleStart;
        this.subTitleStart = subTitleStart;
        this.titleEnd = titleEnd;
        this.subTitleEnd = subTitleEnd;
    }

    public static TitleNotification deserialize(ConfigurationSection section) {
        String cTitleStart = section.getString("TitleStart", "");
        String cSubTitleStart = section.getString("SubtitleStart", "");
        String cTitleEnd = section.getString("TitleEnd", "");
        String cSubTitleEnd = section.getString("SubtitleEnd", "");
        return new TitleNotification(cTitleStart, cSubTitleStart, cTitleEnd, cSubTitleEnd);
    }

    public String getTitleStart() {
        return titleStart;
    }

    public String getSubTitleStart() {
        return subTitleStart;
    }

    public String getTitleEnd() {
        return titleEnd;
    }

    public String getSubTitleEnd() {
        return subTitleEnd;
    }
}
