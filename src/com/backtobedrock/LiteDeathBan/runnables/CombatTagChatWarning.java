package com.backtobedrock.LiteDeathBan.runnables;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTagChatWarning extends BukkitRunnable {

    private final LiteDeathBan plugin;
    private final Player plyr;
    private final String taggedBy;

    public CombatTagChatWarning(LiteDeathBan plugin, Player plyr, String taggedBy) {
        this.plugin = plugin;
        this.plyr = plyr;
        this.taggedBy = taggedBy;
    }

    @Override
    public void run() {
        this.plugin.removeFromTagList(plyr.getUniqueId());
        plyr.spigot().sendMessage(new ComponentBuilder(this.plugin.getMessages().getOnCombatTaggedChatEnd(this.plyr.getName(), this.taggedBy, this.plugin.getLDBConfig().getCombatTagTime())).create());
    }
}
