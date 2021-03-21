package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivesAndLifePartsConfiguration {
    //lives
    private final boolean useLives;
    private final int maxLives;
    private final int livesAtStart;
    private final int livesAfterBan;
    private final int livesLostPerDeath;
    private final List<String> disableLosingLivesInWorlds;

    //life parts
    private final boolean useLifeParts;
    private final int maxLifeParts;
    private final int lifePartsPerLife;
    private final int lifePartsAtStart;
    private final int lifePartsAfterBan;
    private final int lifePartsLostPerDeath;
    private final int lifePartsLostPerDeathBan;
    private final boolean lifePartsOnKill;
    private final Map<EntityType, Integer> lifePartsPerKill;
    private final boolean getLifePartsByPlaytime;
    private final int playtimePerLifePart;
    private final List<String> disableGainingLifePartsInWorlds;
    private final List<String> disableLosingLifePartsInWorlds;

    public LivesAndLifePartsConfiguration(
            //lives
            boolean useLives,
            int maxLives,
            int livesAtStart,
            int livesAfterBan,
            int livesLostPerDeath,
            List<String> disableLosingLivesInWorlds,
            //life parts
            boolean useLifeParts,
            int maxLifeParts,
            int lifePartsPerLife,
            int lifePartsAtStart,
            int lifePartsAfterBan,
            int lifePartsLostPerDeath,
            int lifePartsLostPerDeathBan,
            boolean lifePartsOnKill,
            Map<EntityType, Integer> lifePartsPerKill,
            boolean getLifePartsByPlaytime,
            int playtimePerLifePart,
            List<String> disableGainingLifePartsInWorlds,
            List<String> disableLosingLifePartsInWorlds) {
        //lives
        this.useLives = useLives;
        this.maxLives = maxLives;
        this.livesAtStart = livesAtStart;
        this.livesAfterBan = livesAfterBan;
        this.livesLostPerDeath = livesLostPerDeath;
        this.disableLosingLivesInWorlds = disableLosingLivesInWorlds;

        //life parts
        this.useLifeParts = useLifeParts;
        this.maxLifeParts = maxLifeParts;
        this.lifePartsPerLife = lifePartsPerLife;
        this.lifePartsAtStart = lifePartsAtStart;
        this.lifePartsAfterBan = lifePartsAfterBan;
        this.lifePartsLostPerDeath = lifePartsLostPerDeath;
        this.lifePartsLostPerDeathBan = lifePartsLostPerDeathBan;
        this.lifePartsOnKill = lifePartsOnKill;
        this.lifePartsPerKill = lifePartsPerKill;
        this.getLifePartsByPlaytime = getLifePartsByPlaytime;
        this.playtimePerLifePart = playtimePerLifePart;
        this.disableGainingLifePartsInWorlds = disableGainingLifePartsInWorlds;
        this.disableLosingLifePartsInWorlds = disableLosingLifePartsInWorlds;
    }

    public static LivesAndLifePartsConfiguration deserialize(ConfigurationSection section) {
        //lives
        boolean cUseLives = section.getBoolean("UseLives", true);
        int cMaxLives = cUseLives ? ConfigUtils.checkMin("MaxLives", section.getInt("MaxLives", 5), 1) : 1;
        int cLivesAtStart = cUseLives ? ConfigUtils.checkMin("LivesAtStart", section.getInt("LivesAtStart", 1), 1) : 1;
        int cLivesAfterBan = cUseLives ? ConfigUtils.checkMin("LivesAfterBan", section.getInt("LivesAfterBan", 1), 1) : 1;
        int cLivesLostPerDeath = cUseLives ? ConfigUtils.checkMin("LivesLostPerDeath", section.getInt("LivesLostPerDeath", 1), 1) : 1;
        List<String> cDisableLosingLivesInWorlds = cUseLives ? ConfigUtils.getWorlds("DisableLosingLivesInWorlds", section.getStringList("DisableLosingLivesInWorlds")) : new ArrayList<>();

        //life parts
        boolean cUseLifeParts = cUseLives && section.getBoolean("UseLifeParts", true);
        int cMaxLifeParts = cUseLifeParts ? ConfigUtils.checkMin("MaxLifeParts", section.getInt("MaxLifeParts", 6), 0) : 0;
        int cLifePartsPerLife = cUseLifeParts ? ConfigUtils.checkMin("LifePartsPerLife", section.getInt("LifePartsPerLife"), 1) : 0;
        int cLifePartsAtStart = cUseLifeParts ? ConfigUtils.checkMin("LifePartsAtStart", section.getInt("LifePartsAtStart"), 0) : 0;
        int cLifePartsAfterBan = cUseLifeParts ? ConfigUtils.checkMin("LifePartsAfterBan", section.getInt("LifePartsAfterBan"), -1) : 0;
        int cLifePartsLostPerDeath = cUseLifeParts ? ConfigUtils.checkMin("LifePartsLostPerDeath", section.getInt("LifePartsLostPerDeath", 1), -1) : 0;
        int cLifePartsLostPerDeathBan = cUseLifeParts ? ConfigUtils.checkMin("LifePartsLostPerDeathBan", section.getInt("LifePartsLostPerDeathBan", -1), -1) : 0;
        boolean cLifePartsOnKill = cUseLifeParts && section.getBoolean("LifePartsOnKill");
        Map<EntityType, Integer> cLifePartsPerKill = new HashMap<>();
        boolean cGetLifePartsByPlaytime = cUseLifeParts && section.getBoolean("GetLifePartByPlaytime", false);
        int cPlaytimePerLifePart = ConfigUtils.checkMin("PlaytimePerLifePart", section.getInt("PlaytimePerLifePart", 30), 1);
        List<String> cDisableGainingLifePartsInWorlds = cUseLifeParts ? ConfigUtils.getWorlds("DisableGainingLifePartsInWorlds", section.getStringList("DisableGainingLifePartsInWorlds")) : new ArrayList<>();
        List<String> cDisableLosingLifePartsInWorlds = cUseLifeParts ? ConfigUtils.getWorlds("DisableLosingLifePartsInWorlds", section.getStringList("DisableLosingLifePartsInWorlds")) : new ArrayList<>();

        //if cLifePartsLostPerDeath or cLifePartsLostPerDeathBan == -1 then set to max Integer.
        if (cLifePartsLostPerDeath == -1) {
            cLifePartsLostPerDeath = Integer.MAX_VALUE;
        }
        if (cLifePartsLostPerDeathBan == -1) {
            cLifePartsLostPerDeathBan = Integer.MAX_VALUE;
        }
        if (cMaxLifeParts == -1) {
            cMaxLifeParts = Integer.MAX_VALUE;
        }

        ConfigurationSection lifePartsPerKillSection = section.getConfigurationSection("LifePartsPerKill");
        if (lifePartsPerKillSection != null) {
            lifePartsPerKillSection.getKeys(false).forEach(e -> {
                EntityType type = ConfigUtils.getLivingEntityType("LifePartsPerKill", e);
                if (type != null) {
                    int amount = ConfigUtils.checkMin("LifePartsPerKill." + e, lifePartsPerKillSection.getInt(e, 0), 0);
                    if (amount != -10)
                        cLifePartsPerKill.put(type, amount);
                }
            });
        }

        if (cMaxLives == -10 || cLivesAtStart == -10 || cLivesAfterBan == -10 || cLivesLostPerDeath == -10 || cMaxLifeParts == -10 || cLifePartsPerLife == -10 || cLifePartsAtStart == -10 || cLifePartsAfterBan == -10 || cLifePartsLostPerDeath == -10 || cLifePartsLostPerDeathBan == -10 || cPlaytimePerLifePart == -10) {
            return null;
        }

        return new LivesAndLifePartsConfiguration(
                //lives
                cUseLives,
                cMaxLives,
                cLivesAtStart,
                cLivesAfterBan,
                cLivesLostPerDeath,
                cDisableLosingLivesInWorlds,
                //life parts
                cUseLifeParts,
                cMaxLifeParts,
                cLifePartsPerLife,
                cLifePartsAtStart,
                cLifePartsAfterBan,
                cLifePartsLostPerDeath,
                cLifePartsLostPerDeathBan,
                cLifePartsOnKill,
                cLifePartsPerKill,
                cGetLifePartsByPlaytime,
                cPlaytimePerLifePart * 60,
                cDisableGainingLifePartsInWorlds,
                cDisableLosingLifePartsInWorlds
        );
    }

    public int getLifePartsAfterBan() {
        return lifePartsAfterBan;
    }

    public int getLivesAfterBan() {
        return livesAfterBan;
    }

    public int getLifePartsAtStart() {
        return lifePartsAtStart;
    }

    public boolean isUseLives() {
        return useLives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getLivesAtStart() {
        return livesAtStart;
    }

    public int getLivesLostPerDeath() {
        return livesLostPerDeath;
    }

    public List<String> getDisableLosingLivesInWorlds() {
        return disableLosingLivesInWorlds;
    }

    public boolean isUseLifeParts() {
        return useLifeParts;
    }

    public int getLifePartsPerLife() {
        return lifePartsPerLife;
    }

    public int getLifePartsLostPerDeath() {
        return lifePartsLostPerDeath;
    }

    public boolean isLifePartsOnKill() {
        return lifePartsOnKill;
    }

    public boolean isGetLifePartsByPlaytime() {
        return getLifePartsByPlaytime;
    }

    public int getPlaytimePerLifePart() {
        return playtimePerLifePart;
    }

    public List<String> getDisableLosingLifePartsInWorlds() {
        return disableLosingLifePartsInWorlds;
    }

    public int getLifePartsLostPerDeathBan() {
        return lifePartsLostPerDeathBan;
    }

    public Map<EntityType, Integer> getLifePartsPerKill() {
        return lifePartsPerKill;
    }

    public List<String> getDisableGainingLifePartsInWorlds() {
        return disableGainingLifePartsInWorlds;
    }

    public int getMaxLifeParts() {
        return maxLifeParts;
    }
}
