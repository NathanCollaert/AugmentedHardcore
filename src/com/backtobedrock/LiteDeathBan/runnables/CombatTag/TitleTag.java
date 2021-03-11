package com.backtobedrock.LiteDeathBan.runnables.CombatTag;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.notifications.TitleNotification;
import org.bukkit.entity.Player;

public class TitleTag extends AbstractCombatTag {
    private final TitleNotification titleNotification;

    public TitleTag(Player player, PlayerData playerData, Killer tagger, TitleNotification titleNotification) {
        super(player, playerData, tagger);
        this.titleNotification = titleNotification;
    }

    @Override
    public void start() {
        super.start();
        this.player.sendTitle(this.titleNotification.getTitleStart(), this.titleNotification.getSubTitleStart(), 10, 70, 20);
    }

    @Override
    public void stop() {
        super.stop();
        this.player.sendTitle(this.titleNotification.getTitleEnd(), this.titleNotification.getSubTitleEnd(), 10, 70, 20);
    }
}
