package com.backtobedrock.LiteDeathBan;

import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class LiteDeathBanConfig {

    private final FileConfiguration config;
    private Logger log = Bukkit.getLogger();

    private int PlayerDeathBantime;
    private int MonsterDeathBantime;
    private int EnvironmentDeathBantime;
    private int BantimeByPlaytimePercent;
    private int BantimeByPlaytimeInterval;
    private int BantimeByPlaytimeMinimumPlayerDeath;
    private int BantimeByPlaytimeMinimumMonsterDeath;
    private int BantimeByPlaytimeMinimumEnvironmentDeath;
    private int CombatTagTime;
    private boolean NotifyOnRespawn;
    private boolean BantimeByPlaytime;
    private boolean CombatTag;
    private String BantimeByPlaytimeGrowth;
    private String CombatTagWarningStyle;

    public LiteDeathBanConfig(FileConfiguration fc) {
        this.config = fc;
        this.initialize();
    }

    private void initialize() {
        this.BantimeByPlaytime = this.config.getBoolean("BantimeByPlaytime", false);
        this.CombatTag = this.config.getBoolean("CombatTag", true);
        for (Map.Entry<String, Object> e : this.config.getValues(true).entrySet()) {
            switch (e.getKey()) {
                case "PlayerDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.PlayerDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 7200);
                    } else {
                        if (!this.BantimeByPlaytime) {
                            this.PlayerDeathBantime = 7200;
                            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 7200));
                        } else {
                            this.PlayerDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 7200);
                        }
                    }
                    break;
                case "MonsterDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.MonsterDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 2880);
                    } else {
                        if (!this.BantimeByPlaytime) {
                            this.MonsterDeathBantime = 2880;
                            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 2880));
                        } else {
                            this.MonsterDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 2880);
                        }
                    }
                    break;
                case "EnvironmentDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.EnvironmentDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 4320);
                    } else {
                        if (!this.BantimeByPlaytime) {
                            this.EnvironmentDeathBantime = 4320;
                            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 4320));
                        } else {
                            this.EnvironmentDeathBantime = this.checkMin(e.getKey(), e.getValue(), -1, 4320);
                        }
                    }
                    break;
                case "BantimeByPlaytimePercent":
                    this.BantimeByPlaytimePercent = this.checkMin(e.getKey(), e.getValue(), 1, 10);
                    break;
                case "BantimeByPlaytimeInterval":
                    this.BantimeByPlaytimeInterval = this.checkMin(e.getKey(), e.getValue(), 1, 60);
                    break;
                case "BantimeByPlaytimeMinimumPlayerDeath":
                    this.BantimeByPlaytimeMinimumPlayerDeath = this.checkMin(e.getKey(), e.getValue(), 1, 72);
                    break;
                case "BantimeByPlaytimeMinimumMonsterDeath":
                    this.BantimeByPlaytimeMinimumMonsterDeath = this.checkMin(e.getKey(), e.getValue(), 1, 28);
                    break;
                case "BantimeByPlaytimeMinimumEnvironmentDeath":
                    this.BantimeByPlaytimeMinimumEnvironmentDeath = this.checkMin(e.getKey(), e.getValue(), 1, 43);
                    break;
                case "CombatTagTime":
                    this.CombatTagTime = this.checkMin(e.getKey(), e.getValue(), 0, 60);
                    if (this.CombatTagTime == 0 && this.CombatTag) {
                        this.CombatTag = false;
                        log.warning(String.format("[LiteDeathBan] CombatDeath has been disabled due to %s being 0.", e.getKey()));
                    }
                    break;
                case "NotifyOnRespawn":
                    this.NotifyOnRespawn = this.checkBoolean(e.getValue(), true, e.getKey());
                    break;
                case "BantimeByPlaytimeGrowth":
                    if (e.getValue().toString().equalsIgnoreCase("linear") || e.getValue().toString().equalsIgnoreCase("exponential")) {
                        this.BantimeByPlaytimeGrowth = e.getValue().toString().toLowerCase();
                    } else {
                        this.BantimeByPlaytimeGrowth = "exponential";
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it not being configured as linear or exponential.", e.getKey(), "exponential"));
                    }
                    break;
                case "CombatTagWarningStyle":
                    String style = e.getValue().toString();
                    if (style.equalsIgnoreCase("chat") || style.equalsIgnoreCase("none") || style.equalsIgnoreCase("bossbar")) {
                        this.CombatTagWarningStyle = style;
                    } else {
                        this.CombatTagWarningStyle = "bossbar";
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it not being configured as linear or exponential.", e.getKey(), "bossbar"));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int checkMin(String key, Object value, int MIN, int defaultValue) {
        int number;
        if (value instanceof Integer) {
            number = (int) value;
        } else {
            number = defaultValue;
            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it not being configured as a number.", key, defaultValue));
        }
        if (number >= MIN) {
            return number;
        } else {
            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being below the minimum value.", key, defaultValue));
            return defaultValue;
        }
    }

    private boolean checkBoolean(Object value, boolean defaultValue, String key) {
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it being true or false.", key, defaultValue));
            return defaultValue;
        }
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

    public boolean isCombatTag() {
        return CombatTag;
    }

    public int getCombatTagTime() {
        return CombatTagTime;
    }

    public boolean isNotifyOnRespawn() {
        return NotifyOnRespawn;
    }

    public String getBantimeByPlaytimeGrowth() {
        return BantimeByPlaytimeGrowth;
    }

    public int getPlayerDeathBantime() {
        return PlayerDeathBantime;
    }

    public int getMonsterDeathBantime() {
        return MonsterDeathBantime;
    }

    public int getEnvironmentDeathBantime() {
        return EnvironmentDeathBantime;
    }

    public int getBantimeByPlaytimeMinimumPlayerDeath() {
        return BantimeByPlaytimeMinimumPlayerDeath;
    }

    public int getBantimeByPlaytimeMinimumMonsterDeath() {
        return BantimeByPlaytimeMinimumMonsterDeath;
    }

    public int getBantimeByPlaytimeMinimumEnvironmentDeath() {
        return BantimeByPlaytimeMinimumEnvironmentDeath;
    }

    public String getCombatTagWarningStyle() {
        return CombatTagWarningStyle;
    }
}
