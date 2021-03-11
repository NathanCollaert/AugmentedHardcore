package com.backtobedrock.LiteDeathBan.runnables.CombatTag;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.notifications.ChatNotification;
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
        this.player.sendMessage(this.chatNotification.getTextStart());
    }

    @Override
    public void stop() {
        super.stop();
        this.player.sendMessage(this.chatNotification.getTextEnd());
    }
}
