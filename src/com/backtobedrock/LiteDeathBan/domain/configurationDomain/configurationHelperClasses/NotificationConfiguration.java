package com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses;

import com.backtobedrock.LiteDeathBan.domain.enums.NotificationType;
import com.backtobedrock.LiteDeathBan.domain.notifications.ActionBarNotification;
import com.backtobedrock.LiteDeathBan.domain.notifications.BossBarNotification;
import com.backtobedrock.LiteDeathBan.domain.notifications.ChatNotification;
import com.backtobedrock.LiteDeathBan.domain.notifications.TitleNotification;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

public class NotificationConfiguration {
    private final ActionBarNotification actionBarNotification;
    private final BossBarNotification bossBarNotification;
    private final ChatNotification chatNotification;
    private final TitleNotification titleNotification;

    public NotificationConfiguration(ActionBarNotification actionBarNotification, BossBarNotification bossBarNotification, ChatNotification chatNotification, TitleNotification titleNotification) {
        this.actionBarNotification = actionBarNotification;
        this.bossBarNotification = bossBarNotification;
        this.chatNotification = chatNotification;
        this.titleNotification = titleNotification;
    }

    public static NotificationConfiguration deserialize(ConfigurationSection section) {
        ActionBarNotification cActionBarNotification = null;
        BossBarNotification cBossBarNotification = null;
        ChatNotification cChatNotification = null;
        TitleNotification cTitleNotification = null;

        for (String e : section.getKeys(false)) {
            NotificationType type = ConfigUtils.getNotifcationType("CombatTagNotification", e);
            if (type != null) {
                ConfigurationSection notificationSection = section.getConfigurationSection(e);
                if (notificationSection != null) {
                    boolean enable = notificationSection.getBoolean("Enable", false);
                    if (enable) {
                        ConfigurationSection configurationSection = notificationSection.getConfigurationSection("Configuration");
                        if (configurationSection != null)
                            switch (type) {
                                case CHAT:
                                    cChatNotification = ChatNotification.deserialize(configurationSection);
                                    break;
                                case BOSSBAR:
                                    cBossBarNotification = BossBarNotification.deserialize("CombatTagNotification." + e, configurationSection);
                                    break;
                                case TITLE:
                                    cTitleNotification = TitleNotification.deserialize(configurationSection);
                                    break;
                                case ACTIONBAR:
                                    cActionBarNotification = ActionBarNotification.deserialize(configurationSection);
                                    break;
                            }
                    }
                }
            }
        }

        return new NotificationConfiguration(cActionBarNotification, cBossBarNotification, cChatNotification, cTitleNotification);
    }

    public ActionBarNotification getActionBarNotification() {
        return actionBarNotification;
    }

    public BossBarNotification getBossBarNotification() {
        return bossBarNotification;
    }

    public ChatNotification getChatNotification() {
        return chatNotification;
    }

    public TitleNotification getTitleNotification() {
        return titleNotification;
    }
}
