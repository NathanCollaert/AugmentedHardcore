package com.backtobedrock.LiteDeathBan;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private int BantimeOnReviveDeath;
    private int TimeBetweenRevives;
    private int MaxLives;
    private int PartsPerKill;
    private int PlaytimePerPart;
    private int PlaytimeCheck;
    private int AmountOfPartsPerLife;
    private boolean BantimeByPlaytime;
    private boolean CombatTag;
    private boolean bantimeByPlaytimeSinceLastDeath;
    private boolean Revive;
    private boolean ReviveOptionOnFirstJoin;
    private boolean CombatTagPlayerKickDeath;
    private boolean CombatTagSelf;
    private boolean ShowLivesInTabMenu;
    private boolean LogDeathBans;
    private boolean LogDeaths;
    private boolean GetPartOfLifeOnKill;
    private boolean PartsLostUponDeath;
    private boolean GetPartOfLifeOnPlaytime;
    private boolean CountPlaytimeFromStart;
    private boolean UseMetrics;
    private String BantimeByPlaytimeGrowth;
    private String CombatTagWarningStyle;
    private DateTimeFormatter saveDateFormat;

    public LiteDeathBanConfig(FileConfiguration fc) {
        this.config = fc;
        this.initialize();
    }

    private void initialize() {
        this.BantimeByPlaytime = this.config.getBoolean("BantimeByPlaytime", false);
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
                case "CombatTag":
                    this.CombatTag = this.checkBoolean(e.getKey(), e.getValue(), true);
                    break;
                case "CombatTagTime":
                    this.CombatTagTime = this.checkMin(e.getKey(), e.getValue(), 1, 10);
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
                case "SaveDateFormat":
                    switch (e.getValue().toString()) {
                        case "short":
                            this.saveDateFormat = DateTimeFormatter.ofPattern("MM/dd/yy',' HH:mm z").withZone(ZoneId.systemDefault());
                            break;
                        case "medium":
                            this.saveDateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm z").withZone(ZoneId.systemDefault());
                            break;
                        case "long":
                            this.saveDateFormat = DateTimeFormatter.ofPattern("EEEE MMM dd yyyy 'at' HH:mm:ss z").withZone(ZoneId.systemDefault());
                            break;
                        default:
                            this.saveDateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm:ss z").withZone(ZoneId.systemDefault());
                            log.warning(String.format("[LiteDeathBan] %s has been changed to its default value (%s) due it not being configured as short, medium or long.", e.getKey(), "medium"));
                            break;
                    }
                    break;
                case "BantimeByPlaytimeSinceLastDeath":
                    if (this.BantimeByPlaytime) {
                        this.bantimeByPlaytimeSinceLastDeath = this.checkBoolean(e.getKey(), e.getValue(), false);
                    } else {
                        this.bantimeByPlaytimeSinceLastDeath = false;
                    }
                    break;
                case "Revive":
                    this.Revive = this.checkBoolean(e.getKey(), e.getValue(), true);
                    break;
                case "BantimeOnReviveDeath":
                    this.BantimeOnReviveDeath = this.checkMin(e.getKey(), e.getValue(), 1, 7200);
                    break;
                case "TimeBetweenRevives":
                    this.TimeBetweenRevives = this.checkMin(e.getKey(), e.getValue(), 0, 1440);
                    break;
                case "ReviveOptionOnFirstJoin":
                    this.ReviveOptionOnFirstJoin = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "MaxLives":
                    if (e.getValue() instanceof Integer && (int) e.getValue() == 0) {
                        this.MaxLives = Integer.MAX_VALUE;
                    } else {
                        this.MaxLives = this.checkMin(e.getKey(), e.getValue(), 1, 5);
                    }
                    break;
                case "CombatTagPlayerKickDeath":
                    this.CombatTagPlayerKickDeath = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "CombatTagSelf":
                    this.CombatTagSelf = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "ShowLivesInTabMenu":
                    this.ShowLivesInTabMenu = this.checkBoolean(e.getKey(), e.getValue(), true);
                    break;
                case "LogDeathBans":
                    this.LogDeathBans = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "LogDeaths":
                    this.LogDeaths = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "GetPartOfLifeOnKill":
                    this.GetPartOfLifeOnKill = this.checkBoolean(e.getKey(), e.getValue(), true);
                    break;
                case "PartsPerKill":
                    this.PartsPerKill = this.checkMin(e.getKey(), e.getValue(), 1, 1);
                    break;
                case "PartsLostUponDeath":
                    this.PartsLostUponDeath = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "GetPartOfLifeOnPlaytime":
                    this.GetPartOfLifeOnPlaytime = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "PlaytimePerPart":
                    this.PlaytimePerPart = this.checkMin(e.getKey(), e.getValue(), 1, 60);
                    break;
                case "PlaytimeCheck":
                    this.PlaytimeCheck = this.checkMin(e.getKey(), e.getValue(), 1, 60);
                    break;
                case "AmountOfPartsPerLife":
                    this.AmountOfPartsPerLife = this.checkMin(e.getKey(), e.getValue(), 1, 5);
                    break;
                case "CountPlaytimeFromStart":
                    this.CountPlaytimeFromStart = this.checkBoolean(e.getKey(), e.getValue(), false);
                    break;
                case "UseMetrics":
                    this.UseMetrics = this.checkBoolean(e.getKey(), e.getValue(), true);
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

    private boolean checkBoolean(String key, Object value, boolean defaultValue) {
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

    public boolean isBantimeByPlaytimeSinceLastDeath() {
        return bantimeByPlaytimeSinceLastDeath;
    }

    public DateTimeFormatter getSaveDateFormat() {
        return saveDateFormat;
    }

    public int getBantimeOnReviveDeath() {
        return BantimeOnReviveDeath;
    }

    public boolean isRevive() {
        return Revive;
    }

    public int getTimeBetweenRevives() {
        return TimeBetweenRevives;
    }

    public boolean isReviveOptionOnFirstJoin() {
        return ReviveOptionOnFirstJoin;
    }

    public int getMaxLives() {
        return MaxLives;
    }

    public boolean isCombatTagPlayerKickDeath() {
        return CombatTagPlayerKickDeath;
    }

    public boolean isCombatTagSelf() {
        return CombatTagSelf;
    }

    public boolean isShowLivesInTabMenu() {
        return ShowLivesInTabMenu;
    }

    public boolean isLogDeathBans() {
        return LogDeathBans;
    }

    public boolean isLogDeaths() {
        return LogDeaths;
    }

    public int getPartsPerKill() {
        return PartsPerKill;
    }

    public int getPlaytimePerPart() {
        return PlaytimePerPart;
    }

    public int getPlaytimeCheck() {
        return PlaytimeCheck;
    }

    public int getAmountOfPartsPerLife() {
        return AmountOfPartsPerLife;
    }

    public boolean isGetPartOfLifeOnKill() {
        return GetPartOfLifeOnKill;
    }

    public boolean isPartsLostUponDeath() {
        return PartsLostUponDeath;
    }

    public boolean isGetPartOfLifeOnPlaytime() {
        return GetPartOfLifeOnPlaytime;
    }

    public boolean isCountPlaytimeFromStart() {
        return CountPlaytimeFromStart;
    }

    public boolean isUseMetrics() {
        return UseMetrics;
    }
}
