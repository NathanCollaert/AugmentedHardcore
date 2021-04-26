package com.backtobedrock.augmentedhardcore.runnables.CombatTag;

import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.notifications.TitleNotification;
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
        this.player.sendTitle(this.placeholderReplacements(this.titleNotification.getTitleStart()), this.placeholderReplacements(this.titleNotification.getSubTitleStart()), 10, 70, 20);
    }

    @Override
    public void stop() {
        super.stop();
        this.player.sendTitle(this.placeholderReplacements(this.titleNotification.getTitleEnd()), this.placeholderReplacements(this.titleNotification.getSubTitleEnd()), 10, 70, 20);
    }
}
