package com.backtobedrock.augmentedhardcore.eventListeners.dependencies;

import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.api.event.PlayerReTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.eventListeners.AbstractEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class ListenerCombatLogX extends AbstractEventListener {

    @EventHandler
    public void onPlayerCombatTag(PlayerTagEvent event) {
        System.out.println("test 1");
        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(playerData -> playerData.setCombatTagger(new Killer(event.getEnemy().getName(), event.getEnemy().getCustomName(), event.getEnemy().getType())));
    }

    @EventHandler
    public void onPlayerUnCombatTag(PlayerUntagEvent event) {
        System.out.println("test 2");
        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(playerData -> playerData.setCombatTagger(null));
    }

    @EventHandler
    public void onPlayerReCombatTag(PlayerReTagEvent event) {
        System.out.println("test 3");
        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(playerData -> playerData.setCombatTagger(new Killer(event.getEnemy().getName(), event.getEnemy().getCustomName(), event.getEnemy().getType())));
    }

    @EventHandler
    public void onPlayerLeaveCombatTagged(PlayerPunishEvent event) {
        System.out.println("test 4");
        if (event.isCancelled()) {
            return;
        }

        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(playerData -> playerData.setCombatLogged(true));
    }

    @Override
    public boolean isEnabled() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatLogX");
        boolean enabled = plugin != null && plugin.isEnabled();
        if (enabled) {
            this.plugin.getConfigurations().getCombatTagConfiguration().setPlayerCombatTag(false);
            this.plugin.getConfigurations().getCombatTagConfiguration().setMonsterCombatTag(false);
            this.plugin.getLogger().log(Level.WARNING, "Found CombatLogX, combat logging will be handled by it.");
        }
        return enabled;
    }
}
