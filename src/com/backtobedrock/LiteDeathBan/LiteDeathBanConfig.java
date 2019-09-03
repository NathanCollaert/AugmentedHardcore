package com.backtobedrock.LiteDeathBan;

import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author PC_Nathan
 */
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
    private int CombatLogTime;
    private boolean NotifyLivesLeftOnRespawn;
    private boolean BantimeByPlaytime;
    private boolean CombatLog;
    private String BantimeByPlaytimeGrowth;
    private String CombatLogWarningStyle;

    public LiteDeathBanConfig(FileConfiguration fc) {
        this.config = fc;
        this.initialize();
    }

    private void initialize() {
        this.BantimeByPlaytime = this.checkBoolean(this.config.get("BantimeByPlaytime"), false, "BantimeByPlaytime");
        this.CombatLog = this.checkBoolean(this.config.get("CombatLog"), true, "CombatLog");
        for (Map.Entry<String, Object> e : this.config.getValues(true).entrySet()) {
            switch (e.getKey()) {
                case "PlayerDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.PlayerDeathBantime = this.checkMin(-1, e.getKey(), e.getValue(), 7200);
                    } else {
                        this.PlayerDeathBantime = 7200;
                        this.config.set("PlayerKillBantime", 7200);
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 7200));
                    }
                    break;
                case "MonsterDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.MonsterDeathBantime = this.checkMin(-1, e.getKey(), e.getValue(), 2880);
                    } else {
                        this.MonsterDeathBantime = 2880;
                        this.config.set("MonsterKillBantime", 2880);
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 2880));
                    }
                    break;
                case "EnvironmentDeathBantime":
                    if (!e.getValue().equals(0)) {
                        this.EnvironmentDeathBantime = this.checkMin(-1, e.getKey(), e.getValue(), 7200);
                    } else {
                        this.EnvironmentDeathBantime = 4320;
                        this.config.set("MonsterKillBantime", 4320);
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being 0.", e.getKey(), 4320));
                    }
                    break;
                case "BantimeByPlaytimePercent":
                    this.BantimeByPlaytimePercent = this.checkMin(1, e.getKey(), e.getValue(), 10);
                    break;
                case "BantimeByPlaytimeInterval":
                    this.BantimeByPlaytimeInterval = this.checkMin(1, e.getKey(), e.getValue(), 60);
                    break;
                case "BantimeByPlaytimeMinimumPlayerDeath":
                    this.BantimeByPlaytimeMinimumPlayerDeath = this.checkMin(1, e.getKey(), e.getValue(), 72);
                    break;
                case "BantimeByPlaytimeMinimumMonsterDeath":
                    this.BantimeByPlaytimeMinimumMonsterDeath = this.checkMin(1, e.getKey(), e.getValue(), 28);
                    break;
                case "BantimeByPlaytimeMinimumEnvironmentDeath":
                    this.BantimeByPlaytimeMinimumEnvironmentDeath = this.checkMin(1, e.getKey(), e.getValue(), 43);
                    break;
                case "CombatLogTime":
                    this.CombatLogTime = this.checkMin(0, e.getKey(), e.getValue(), 60);
                    if (this.CombatLogTime == 0 && this.CombatLog) {
                        this.CombatLog = false;
                        log.warning(String.format("[LiteDeathBan] CombatDeath has been disabled due to %s being 0.", e.getKey()));
                    }
                    break;
                case "NotifyLivesLeftOnRespawn":
                    this.NotifyLivesLeftOnRespawn = this.checkBoolean(e.getValue(), true, e.getKey());
                    break;
                case "BantimeByPlaytimeGrowth":
                    if (e.getValue().toString().equalsIgnoreCase("linear") || e.getValue().toString().equalsIgnoreCase("exponential")) {
                        this.BantimeByPlaytimeGrowth = e.getValue().toString().toLowerCase();
                    } else {
                        this.BantimeByPlaytimeGrowth = "exponential";
                        this.config.set("BantimeByPlaytimeGrowth", "exponential");
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it not being configured as Linear or Exponential.", e.getKey(), "exponential"));
                    }
                    break;
                case "CombatLogWarningStyle":
                    String style = e.getValue().toString();
                    if (style.equalsIgnoreCase("chat") || style.equalsIgnoreCase("none") || style.equalsIgnoreCase("bossbar")) {
                        this.CombatLogWarningStyle = style;
                    } else {
                        this.CombatLogWarningStyle = "bossbar";
                        this.config.set("CombatLogWarningStyle", "bossbar");
                        log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it not being configured as Linear or Exponential.", e.getKey(), "bossbar"));
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
            this.config.set(key, defaultValue);
            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it not being configured as a number.", key, defaultValue));
        }
        if (number >= MIN) {
            return number;
        } else {
            this.config.set(key, defaultValue);
            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%d) due it being below the minimum value.", key, defaultValue));
            return defaultValue;
        }
    }

    private boolean checkBoolean(Object value, boolean defaultValue, String key) {
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            this.config.set(key, defaultValue);
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

    public boolean isCombatLog() {
        return CombatLog;
    }

    public int getCombatLogTime() {
        return CombatLogTime;
    }

    public boolean isNotifyLivesLeftOnRespawn() {
        return NotifyLivesLeftOnRespawn;
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

    public String getCombatLogWarningStyle() {
        return CombatLogWarningStyle;
    }

}
