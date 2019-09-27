package com.backtobedrock.LiteDeathBan;

import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LiteDeathBanConfig {

    private final FileConfiguration config;

    private final LiteDeathBan plugin;

    public LiteDeathBanConfig(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.config = this.checkConfigVersion();
    }

    // <editor-fold desc="Miscellaneous" defaultstate="collapsed">
    public boolean isUpdateChecker() {
        return this.config.getBoolean("UpdateChecker", true);
    }
    // </editor-fold>

    // <editor-fold desc="Lives" defaultstate="collapsed">
    public int getMaxLives() {
        int lives = this.config.getInt("MaxLives", 5);
        return lives == 0 ? Integer.MAX_VALUE : this.checkMin(lives, 1, 5);
    }

    public int getLivesAtStart() {
        return this.checkMin(this.config.getInt("LivesAtStart", 1), 1, 1);
    }

    public List<String> getDisableLosingLivesInWorlds() {
        return this.config.getStringList("DisableLosingLivesInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public boolean isDisableDyingInDisabledWorlds() {
        return this.config.getBoolean("DisableDyingInDisabledWorlds", false);
    }
    // </editor-fold>

    // <editor-fold desc="Ban Times" defaultstate="collapsed">
    public int getPlayerDeathBantime() {
        return this.checkBantime(this.config.getInt("PlayerDeathBantime", 7200), 7200);
    }

    public int getMonsterDeathBantime() {
        return this.checkBantime(this.config.getInt("MonsterDeathBantime", 2880), 2880);
    }

    public int getEnvironmentDeathBantime() {
        return this.checkBantime(this.config.getInt("EnvironmentDeathBantime", 4320), 4320);
    }

    public List<String> getDisableBanInWorlds() {
        return this.config.getStringList("DisableBanInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    private int checkBantime(int value, int defaultValue) {
        if (value != 0) {
            return this.checkMin(value, -1, defaultValue);
        } else {
            if (!this.isBantimeByPlaytime()) {
                return defaultValue;
            } else {
                return value;
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="Ban By Playtime" defaultstate="collapsed">
    public boolean isBantimeByPlaytime() {
        return this.config.getBoolean("BantimeByPlaytime", false);
    }

    public boolean isBantimeByPlaytimeSinceLastDeath() {
        return this.config.getBoolean("BantimeByPlaytimeSinceLastDeath", false);
    }

    public int getBantimeByPlaytimePercent() {
        return this.checkMin(this.config.getInt("BantimeByPlaytimePercent", 10), 1, 10);
    }

    public int getBantimeByPlaytimeInterval() {
        return this.checkMin(this.config.getInt("BantimeByPlaytimeInterval", 60), 1, 60);
    }

    public String getBantimeByPlaytimeGrowth() {
        String value = this.config.getString("BantimeByPlaytimeGrowth", "exponential");
        return (value != null && (value.equalsIgnoreCase("linear") || value.equalsIgnoreCase("exponential"))) ? value : "exponential";
    }

    public int getBantimeByPlaytimeMinimumPlayerDeath() {
        return this.checkMin(this.config.getInt("BantimeByPlaytimeMinimumPlayerDeath", 72), 1, 72);
    }

    public int getBantimeByPlaytimeMinimumMonsterDeath() {
        return this.checkMin(this.config.getInt("BantimeByPlaytimeMinimumMonsterDeath", 28), 1, 28);
    }

    public int getBantimeByPlaytimeMinimumEnvironmentDeath() {
        return this.checkMin(this.config.getInt("BantimeByPlaytimeMinimumEnvironmentDeath", 43), 1, 43);
    }
    // </editor-fold>

    // <editor-fold desc="Combat Tag" defaultstate="collapsed">
    public boolean isCombatTag() {
        return this.config.getBoolean("CombatTag", true);
    }

    public boolean isCombatTagSelf() {
        return this.config.getBoolean("CombatTagSelf", false);
    }

    public int getCombatTagTime() {
        return this.checkMin(this.config.getInt("CombatTagTime", 10), 1, 10);
    }

    public String getCombatTagWarningStyle() {
        String value = this.config.getString("CombatTagWarningStyle", "bossbar");
        return (value != null && (value.equalsIgnoreCase("chat") || value.equalsIgnoreCase("none") || value.equalsIgnoreCase("bossbar"))) ? value : "bossbar";

    }

    public boolean isCombatTagPlayerKickDeath() {
        return this.config.getBoolean("CombatTagPlayerKickDeath", false);
    }

    public List<String> getDisableCombatTagInWorlds() {
        return this.config.getStringList("DisableCombatTagInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
    // </editor-fold>

    // <editor-fold desc="Reviving" defaultstate="collapsed">
    public boolean isRevive() {
        return this.config.getBoolean("Revive", true);
    }

    public int getBantimeOnReviveDeath() {
        return this.checkMin(this.config.getInt("BantimeOnReviveDeath", 7200), 1, 7200);
    }

    public int getTimeBetweenRevives() {
        return this.checkMin(this.config.getInt("TimeBetweenRevives", 1440), 0, 1440);
    }

    public boolean isReviveOptionOnFirstJoin() {
        return this.config.getBoolean("ReviveOptionOnFirstJoin", false);
    }

    public List<String> getDisableReviveInWorlds() {
        return this.config.getStringList("DisableReviveInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
    // </editor-fold>

    // <editor-fold desc="Life Parts" defaultstate="collapsed">
    public boolean isGetPartOfLifeOnKill() {
        return this.config.getBoolean("GetPartOfLifeOnKill", true);
    }

    public int getPartsPerKill() {
        return this.checkMin(this.config.getInt("PartsPerKill", 1), 1, 1);
    }

    public boolean isPartsLostUponDeath() {
        return this.config.getBoolean("PartsLostUponDeath", false);
    }

    public boolean isGetPartOfLifeOnPlaytime() {
        return this.config.getBoolean("GetPartOfLifeOnPlaytime", false);
    }

    public boolean isCountPlaytimeFromStart() {
        return this.config.getBoolean("CountPlaytimeFromStart", false);
    }

    public int getPlaytimePerPart() {
        return this.checkMin(this.config.getInt("PlaytimePerPart", 60), 1, 60);
    }

    public int getPlaytimeCheck() {
        return this.checkMin(this.config.getInt("PlaytimeCheck", 60), 1, 60);
    }

    public int getAmountOfPartsPerLife() {
        return this.checkMin(this.config.getInt("AmountOfPartsPerLife", 5), 1, 5);
    }

    public List<String> getDisableGettingLifePartsInWorlds() {
        return this.config.getStringList("DisableGettingLifePartsInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public List<String> getDisableLosingLifePartsInWorlds() {
        return this.config.getStringList("DisableLosingLifePartsInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
    // </editor-fold>

    // <editor-fold desc="Logging" defaultstate="collapsed">
    public boolean isLogDeathBans() {
        return this.config.getBoolean("LogDeathBans", false);
    }

    public boolean isLogDeaths() {
        return this.config.getBoolean("LogDeaths", false);
    }

    public List<String> getDisableLoggingDeathBansInWorlds() {
        return this.config.getStringList("DisableLoggingDeathBansInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public List<String> getDisableLoggingDeathsInWorlds() {
        return this.config.getStringList("DisableLoggingDeathsInWorlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
    // </editor-fold>

    // <editor-fold desc="Format" defaultstate="collapsed">
    public DateTimeFormatter getSaveDateFormat() {
        String value = this.config.getString("SaveDateFormat", "medium");
        switch (value.toLowerCase()) {
            case "short":
                return DateTimeFormatter.ofPattern("MM/dd/yy',' HH:mm z").withZone(ZoneId.systemDefault());
            case "medium":
                return DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm z").withZone(ZoneId.systemDefault());
            case "long":
                return DateTimeFormatter.ofPattern("EEEE MMM dd yyyy 'at' HH:mm:ss z").withZone(ZoneId.systemDefault());
            default:
                return DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm:ss z").withZone(ZoneId.systemDefault());
        }
    }
    // </editor-fold>

    private int checkMin(int value, int min, int defaultValue) {
        if (value >= min) {
            return value;
        } else {
            return defaultValue;
        }
    }

    private FileConfiguration checkConfigVersion() {
        //Load current config and default one.
        FileConfiguration currentConfig = this.plugin.getConfig();
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(this.plugin.getResource("config.yml")));

        //If default config has a different amount of keys, send error.
        if (!currentConfig.getKeys(true).equals(defaultConfig.getKeys(true))) {
            Bukkit.getLogger().severe("[LiteDeathBan] Detected old config file, please regenerate your config file to configure everything correctly! Default values are being used for new options.");
        }
        return this.plugin.getConfig();
    }
}
