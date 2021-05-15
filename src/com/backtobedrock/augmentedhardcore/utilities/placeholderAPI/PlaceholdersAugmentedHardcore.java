package com.backtobedrock.augmentedhardcore.utilities.placeholderAPI;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdersAugmentedHardcore extends PlaceholderExpansion {
    private final AugmentedHardcore plugin;

    public PlaceholdersAugmentedHardcore() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public boolean canRegister() {
        return this.plugin != null;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        ServerData serverData = this.plugin.getServerRepository().getServerDataSync();

        if (serverData != null) {
            switch (identifier) {
                case "server_ongoing_bans":
                    return Integer.toString(serverData.getTotalOngoingBans());
                case "server_total_bans":
                    return Integer.toString(serverData.getTotalDeathBans());
            }
        }

        if (player.getPlayer() != null) {
            PlayerData playerData = this.plugin.getPlayerRepository().getByPlayerSync(player);
            if (playerData != null) {
                switch (identifier) {
                    case "time_till_next_max_health_short":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.SHORT);
                    case "time_till_next_life_part_short":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.SHORT);
                    case "time_till_next_revive_short":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.SHORT);
                    case "time_till_next_max_health_long":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.LONG);
                    case "time_till_next_life_part_long":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.LONG);
                    case "time_till_next_revive_long":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.LONG);
                    case "time_till_next_max_health_digital":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.DIGITAL);
                    case "time_till_next_life_part_digital":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.DIGITAL);
                    case "time_till_next_revive_digital":
                        return MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.DIGITAL);
                    case "life_parts":
                        return Integer.toString(playerData.getLifeParts());
                    case "lives":
                        return Integer.toString(playerData.getLives());
                    case "player_total_bans":
                        return Integer.toString(playerData.getBanCount());
                }
            }
        }

        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
}
