package com.backtobedrock.augmentedhardcore.runnables.CombatTag;

import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.notifications.ChatNotification;
import org.bukkit.entity.Player;

public class ChatTag extends AbstractCombatTag {
    private final ChatNotification chatNotification;

    public ChatTag(Player player, PlayerData playerData, Killer tagger, ChatNotification chatNotification) {
        super(player, playerData, tagger);
        this.chatNotification = chatNotification;
    }

    @Override
    public void start() {
        super.start();
        this.player.sendMessage(this.placeholderReplacements(this.chatNotification.getTextStart()));
    }

    @Override
    public void stop() {
        super.stop();
        this.player.sendMessage(this.placeholderReplacements(this.chatNotification.getTextEnd()));
    }
}
