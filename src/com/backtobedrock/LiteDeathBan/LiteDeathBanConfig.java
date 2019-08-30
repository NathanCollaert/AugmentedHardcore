package com.backtobedrock.LiteDeathBan;

import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author PC_Nathan
 */
class LiteDeathBanConfig {

    private final FileConfiguration config;
    private Logger log = Bukkit.getLogger();

    private int PlayerKillBantime;
    private int MonsterKillBantime;
    private int EnvironmentKillBantime;
    private boolean BantimeByPlaytime;
    private int BantimeByPlaytimePercent;
    private int BantimeByPlaytimeInterval;
    private int BantimeByPlaytimeMinimumTime;
    private boolean CombatDeath;
    private int CombatDeathTime;

    public LiteDeathBanConfig(FileConfiguration fc) {
        this.config = fc;
        this.initialize();
    }

    private void initialize() {
        this.BantimeByPlaytime = this.checkBoolean(this.config.get("BantimeByPlaytime"), false);
        for (Map.Entry<String, Object> e : this.config.getValues(true).entrySet()) {
            switch (e.getKey()) {
                case "PlayerKillBantime":
                    this.PlayerKillBantime = this.checkMin(this.BantimeByPlaytime ? 0 : -1, e.getKey(), e.getValue(), 7200);
                    break;
                case "MonsterKillBantime":
                    this.MonsterKillBantime = this.checkMin(this.BantimeByPlaytime ? 0 : -1, e.getKey(), e.getValue(), 2880);
                    break;
                case "EnvironmentKillBantime":
                    this.EnvironmentKillBantime = this.checkMin(this.BantimeByPlaytime ? 0 : -1, e.getKey(), e.getValue(), 2880);
                    break;
                case "BantimeByPlaytimePercent":
                    this.BantimeByPlaytimePercent = this.checkMin(1, e.getKey(), e.getValue(), 10);
                    break;
                case "BantimeByPlaytimeInterval":
                    this.BantimeByPlaytimeInterval = this.checkMin(1, e.getKey(), e.getValue(), 60);
                    break;
                case "BantimeByPlaytimeMinimumTime":
                    this.BantimeByPlaytimeMinimumTime = this.checkMin(1, e.getKey(), e.getValue(), 60);
                    break;
                case "CombatDeath":
                    this.CombatDeath = this.checkBoolean(e.getValue(), true);
                    break;
                case "CombatDeathTime":
                    this.CombatDeathTime = this.checkMin(0, e.getKey(), e.getValue(), 60);
                    if (this.CombatDeathTime == 0) {
                        this.CombatDeath = false;
                        log.warning(String.format("CombatDeath has been disabled due to %s being 0.", e.getKey()));
                    }
                    break;
                default:
                    break;
            }
        };
    }

    private int checkMin(int MIN, String key, Object value, int defaultValue) {
        int number;
        if (value instanceof Integer) {
            number = (int) value;
        } else {
            number = defaultValue;
            log.warning(String.format("[LDB] %s has been changed to its default value (%d) due it not being configured as a number.", key, defaultValue));
        }
        if (number >= MIN) {
            return number;
        } else {
            log.warning(String.format("[LDB] %s has been changed to its default value (%d) due it being below the minimum value.", key, defaultValue));
            return defaultValue;
        }
    }

    private boolean checkBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            System.out.println();
            return defaultValue;
        }
    }

    public int getPlayerKillBantime() {
        return PlayerKillBantime;
    }

    public int getMonsterKillBantime() {
        return MonsterKillBantime;
    }

    public int getEnvironmentKillBantime() {
        return EnvironmentKillBantime;
    }

    public boolean isBantimeByPlaytime() {
        return BantimeByPlaytime;
    }

    public int getBantimeByPlaytimePercent() {
        return BantimeByPlaytimePercent;
    }

    public int getBantimeByPlaytimeInterval() {
        return BantimeByPlaytimeInterval;
    }

    public int getBantimeByPlaytimeMinimumTime() {
        return BantimeByPlaytimeMinimumTime;
    }

    public boolean isCombatDeath() {
        return CombatDeath;
    }

    public int getCombatDeathTime() {
        return CombatDeathTime;
    }

}
