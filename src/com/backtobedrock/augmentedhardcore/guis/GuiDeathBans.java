package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GuiDeathBans extends AbstractPaginatedGui {
    private final PlayerData playerData;

    public GuiDeathBans(PlayerData playerData) {
        super(new CustomHolder(playerData.getBanCount(), String.format("%s Death Bans", playerData.getPlayer().getName())), playerData.getBanCount());
        this.playerData = playerData;
        this.initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.setPlayerHead(false);
        this.setData();
    }

    @Override
    protected void setData() {
        super.setData();

        Map<String, String> placeholders = new HashMap<>();
        List<Icon> icons = new ArrayList<>();
        List<Map.Entry<Integer, Ban>> currentData = new ArrayList<>(this.playerData.getBans().entrySet()).subList((this.currentPage - 1) * 28, Math.min(this.currentPage * 28, this.playerData.getBanCount()));

        //loading icons
        for (int i = 0; i < currentData.size(); i++) {
            icons.add(new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList()));
            if (icons.size() == 7) {
                this.customHolder.addRow(icons);
                icons.clear();
            }
        }
        if (!icons.isEmpty()) {
            this.customHolder.addRow(icons);
        }
        this.customHolder.setCurrentRow(1);
        icons.clear();

        //load data async
        CompletableFuture.runAsync(() -> {
            currentData.forEach(e -> {
                placeholders.put("ban_number", e.getKey().toString());
                placeholders.putAll(e.getValue().getPlaceholdersReplacements());
                icons.add(new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getItem(), placeholders), Collections.emptyList()));
                if (icons.size() == 7) {
                    this.customHolder.addRow(icons);
                    icons.clear();
                }
            });

            if (!icons.isEmpty()) {
                this.customHolder.addRow(icons);
            }

            this.customHolder.updateInvent();
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void setPlayerHead(boolean update) {
        this.setIcon(4, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createPlayerSkull(this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getName(), this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getLore(), this.playerData.getPlayer()), this.getPlayerPlaceholders()), Collections.emptyList()), update);
    }

    private Map<String, String> getPlayerPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        OfflinePlayer player = this.playerData.getPlayer();
        placeholders.put("player_name", player.getName());
        placeholders.put("total_deaths", player.getPlayer() == null ? "-" : Integer.toString(player.getPlayer().getStatistic(Statistic.DEATHS)));
        placeholders.put("total_death_bans", Integer.toString(this.playerData.getBanCount()));
        Ban lastBan = this.playerData.getLastDeathBan();
        placeholders.put("last_ban_time_long", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(this.playerData.getLastDeathBan().getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.LONG));
        placeholders.put("last_ban_time_short", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(this.playerData.getLastDeathBan().getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.SHORT));
        placeholders.put("last_ban_time_digital", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(this.playerData.getLastDeathBan().getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.DIGITAL));
        placeholders.put("last_death_time_long", player.getPlayer() == null ? "-" : MessageUtils.getTimeFromTicks(player.getPlayer().getStatistic(Statistic.TIME_SINCE_DEATH), TimePattern.LONG));
        placeholders.put("last_death_time_short", player.getPlayer() == null ? "-" : MessageUtils.getTimeFromTicks(player.getPlayer().getStatistic(Statistic.TIME_SINCE_DEATH), TimePattern.SHORT));
        placeholders.put("last_death_time_digital", player.getPlayer() == null ? "-" : MessageUtils.getTimeFromTicks(player.getPlayer().getStatistic(Statistic.TIME_SINCE_DEATH), TimePattern.DIGITAL));
        return placeholders;
    }
}
