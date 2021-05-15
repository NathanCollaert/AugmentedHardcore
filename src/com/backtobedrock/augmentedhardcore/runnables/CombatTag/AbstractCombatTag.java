package com.backtobedrock.augmentedhardcore.runnables.CombatTag;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AbstractCombatTag extends BukkitRunnable {

    protected final AugmentedHardcore plugin;
    protected final int time;
    protected final Player player;
    protected final PlayerData playerData;
    protected int timer;
    protected Killer tagger;

    public AbstractCombatTag(Player player, PlayerData playerData, Killer tagger) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.time = this.plugin.getConfigurations().getCombatTagConfiguration().getCombatTagTime() * 20;
        this.timer = this.time;
        this.tagger = tagger;
        this.playerData = playerData;
        this.player = player;
    }

    public void start() {
        this.playerData.setCombatTagger(this.tagger);
        this.runTaskTimerAsynchronously(this.plugin, 0, 1);
    }

    public void stop() {
        this.cancel();
        this.playerData.removeFromCombatTag(this);
        this.playerData.setCombatTagger(null);
    }

    public void restart(Killer tagger) {
        this.timer = this.time;
        this.tagger = tagger;
        this.playerData.setCombatTagger(this.tagger);
    }

    protected void timerTask() {
        //ignored
    }

    @Override
    public void run() {
        if (this.timer > 0) {
            if (this.timer % 20 == 0)
                this.timerTask();
            this.timer--;
        } else {
            this.stop();
        }
    }

    protected String placeholderReplacements(String text) {
        String replacedText = text;
        if (replacedText.contains("%combat_tagger%")) {
            replacedText = replacedText.replaceAll("%combat_tagger%", this.tagger.getFormattedName());
        }
        if (replacedText.contains("%tag_time_left%")) {
            replacedText = replacedText.replaceAll("%tag_time_left%", Integer.toString(this.timer / 20));
        }
        if (replacedText.contains("%total_tag_time%")) {
            replacedText = replacedText.replaceAll("%total_tag_time%", Integer.toString(this.time / 20));
        }
        return ChatColor.translateAlternateColorCodes('&', replacedText);
    }
}
