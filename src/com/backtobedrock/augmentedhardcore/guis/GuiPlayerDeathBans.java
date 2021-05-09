package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.javatuples.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GuiPlayerDeathBans extends AbstractDeathBansGui {
    private final PlayerData playerData;

    public GuiPlayerDeathBans(PlayerData playerData) {
        super(String.format("%s Death Bans", playerData.getPlayer().getName()), playerData.getBanCount());
        playerData.getBans().forEach((key, value) -> this.bans.put(new Pair<>(playerData.getPlayer(), key), value));
        this.playerData = playerData;
        this.initialize();
    }

    @Override
    public void setDataIcon(boolean update) {
        this.setIcon(4, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createPlayerSkull(this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getName(), this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getLore(), this.playerData.getPlayer()), this.getPlaceholders()), Collections.emptyList()), update);
    }

    private Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        OfflinePlayer player = this.playerData.getPlayer();
        placeholders.put("player", player.getName());
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

    @Override
    public Icon getIcon(OfflinePlayer player, Map<String, String> placeholders) {
        return new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getItem(), placeholders), Collections.emptyList());
    }
}
