package com.backtobedrock.augmentedhardcore.domain.notifications;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

public class BossBarNotification {
    private final String text;
    private final BarColor color;
    private final BarStyle style;

    public BossBarNotification(String text, BarColor color, BarStyle style) {
        this.text = text;
        this.color = color;
        this.style = style;
    }

    public static BossBarNotification deserialize(String id, ConfigurationSection section) {
        String cText = section.getString("Text", "");
        BarColor cColor = ConfigUtils.getBarColor(id + ".Color", section.getString("Color", "red"), BarColor.RED);
        BarStyle cStyle = ConfigUtils.getBarStyle(id + ".Style", section.getString("Style", "solid"), BarStyle.SOLID);

        if (cColor == null || cStyle == null) {
            return null;
        }

        return new BossBarNotification(cText, cColor, cStyle);
    }

    public String getText() {
        return text;
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }
}
