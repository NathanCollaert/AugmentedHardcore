package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.domain.observer.*;
import com.backtobedrock.augmentedhardcore.guis.AbstractGui;
import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;
import com.backtobedrock.augmentedhardcore.runnables.BanExpiration;
import com.backtobedrock.augmentedhardcore.runnables.CombatTag.AbstractCombatTag;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.AbstractPlaytime;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.PlaytimeLifePart;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.PlaytimeMaxHealth;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.PlaytimeRevive;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.EventUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;

import java.util.*;

public class PlayerData {
    //misc
    private final AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    private final OfflinePlayer player;

    //helpers
    private final List<AbstractPlaytime> playtime = new ArrayList<>();
    private final Map<UUID, Map<Class<?>, IObserver>> observers;
    private BanExpiration banExpiration;
    private List<AbstractCombatTag> combatTag = new ArrayList<>();
    private boolean kicked = false;
    private boolean combatLogged = false;
    private Killer combatTagger;
    private Killer reviving;

    //serializable
    private final String lastKnownIp;
    private final NavigableMap<Integer, Ban> bans;
    private int lives;
    private int lifeParts;
    private long timeTillNextRevive;
    private long timeTillNextLifePart;
    private long timeTillNextMaxHealth;
    private boolean spectatorBanned;

    public PlayerData(OfflinePlayer player) {
        this(
                player,
                null,
                JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart(),
                JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart(),
                false,
                JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getReviveConfiguration().isReviveOnFirstJoin() ? 0L : JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getReviveConfiguration().getTimeBetweenRevives(),
                JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart(),
                JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart(),
                new TreeMap<>()
        );
    }

    public PlayerData(OfflinePlayer player, String lastKnownIp, int lives, int lifeParts, boolean spectatorBanned, long timeTillNextRevive, long timeTillNextLifePart, long timeTillNextMaxHealth, NavigableMap<Integer, Ban> bans) {
        this.player = player;
        this.bans = bans;
        this.observers = new HashMap<>();
        this.spectatorBanned = spectatorBanned;
        this.setTimeTillNextLifePart(timeTillNextLifePart);
        this.setTimeTillNextMaxHealth(timeTillNextMaxHealth);
        this.setTimeTillNextRevive(timeTillNextRevive);
        this.setLives(lives);
        this.setLifeParts(lifeParts);
        if (player.getPlayer() != null) {
            if (player.getPlayer().getAddress() != null) {
                lastKnownIp = player.getPlayer().getAddress().getAddress().toString().replaceFirst("/", "");
            }
        }
        this.lastKnownIp = lastKnownIp;
    }

    public static PlayerData deserialize(ConfigurationSection section, OfflinePlayer player) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        NavigableMap<Integer, Ban> cBans = new TreeMap<>();
        String cLastKnownIp = section.getString("LastKnownIp", null);
        int cLives = section.getInt("Lives", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart());
        int cLifeParts = section.getInt("LifeParts", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        boolean cSpectatorBanned = section.getBoolean("SpectatorBanned", false);
        int cTimeTillNextLifePart = section.getInt("TimeTillNextLifePart", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
        int cTimeTillNextMaxHealth = section.getInt("TimeTillNextMaxHealth", plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart());
        long cTimeTillNextRevive = section.getLong("TimeTillNextRevive", plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());

        //get all bans
        ConfigurationSection bansSection = section.getConfigurationSection("Bans");
        if (bansSection != null) {
            for (String e : bansSection.getKeys(false)) {
                ConfigurationSection banSection = bansSection.getConfigurationSection(e);
                if (banSection != null) {
                    cBans.put(Integer.parseInt(e), Ban.Deserialize(banSection));
                }
            }
        }

        return new PlayerData(player, cLastKnownIp, cLives, cLifeParts, cSpectatorBanned, cTimeTillNextRevive, cTimeTillNextLifePart, cTimeTillNextMaxHealth, cBans);
    }

    public long getTimeTillNextRevive() {
        return timeTillNextRevive;
    }

    private void setTimeTillNextRevive(long timeTillNextRevive) {
        this.timeTillNextRevive = Math.max(0, Math.min(timeTillNextRevive, this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives()));
        this.observers.forEach((key, value) -> value.get(MyStatsTimeTillNextReviveObserver.class).update());
    }

    public String getLastKnownIp() {
        return this.lastKnownIp;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(Math.min(lives, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives()), 0);
        this.observers.forEach((key, value) -> value.get(MyStatsLivesObserver.class).update());
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public int getLifeParts() {
        return lifeParts;
    }

    public void setLifeParts(int lifeParts) {
        int lifePartsPerLife = this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsPerLife();
        int lives = Math.min(lifeParts / lifePartsPerLife, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives() - this.getLives());
        if (lives > 0) {
            this.increaseLives(lives);
        }
        this.lifeParts = Math.min(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts(), Math.max(0, lifeParts - (lives * lifePartsPerLife)));
        this.observers.forEach((key, value) -> value.get(MyStatsLifePartsObserver.class).update());
    }

    public NavigableMap<Integer, Ban> getBans() {
        return bans;
    }

    public int getBanCount() {
        return this.bans.size();
    }

    private void decreaseLives(int amount) {
        this.setLives(this.getLives() - amount);
    }

    public void increaseLives(int amount) {
        this.setLives(this.getLives() + amount);
    }

    private void decreaseLifeParts(int amount) {
        this.setLifeParts(this.getLifeParts() - amount);
    }

    public void increaseLifeParts(int amount) {
        this.setLifeParts(this.getLifeParts() + amount);
    }

    public void onDeath(PlayerDeathEvent event) {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() != null && this.plugin.getConfigurations().getMiscellaneousConfiguration().isLightningOnDeath()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> this.player.getPlayer().getWorld().strikeLightningEffect(this.player.getPlayer().getLocation()));
        }

        if (!this.plugin.getConfigurations().getMiscellaneousConfiguration().getCommandsOnDeath().isEmpty() && this.player.getName() != null) {
            this.plugin.getConfigurations().getMiscellaneousConfiguration().getCommandsOnDeath().forEach(e -> Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), e.replaceAll("%player%", this.player.getName()))));
        }

        this.loseLives(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesLostPerDeath());

        this.loseMaxHealth(this.plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealthDecreasePerDeath());

        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives()) {
            if (this.lives == 0) {
                this.loseLifeParts(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeathBan());
                this.ban(event);
            } else {
                this.loseLifeParts(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeath());
            }
        } else {
            this.loseLifeParts(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeath());
            this.ban(event);
        }

        this.unCombatTag();

        this.respawn();

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    private void respawn() {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.player.getPlayer().isDead()) {
            return;
        }

        if (!this.plugin.getConfigurations().getMiscellaneousConfiguration().isDeathScreen()) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                if (this.player.getPlayer() == null) {
                    return;
                }

                this.player.getPlayer().spigot().respawn();
            }, 1L);
        }
    }

    private void loseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            return;
        }

        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSEMAXHEALTH.getPermissionString())) {
            return;
        }

        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableLosingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        this.decreaseMaxHealth(amount);
    }

    private void decreaseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            PlayerUtils.setMaxHealth(this.player.getPlayer(), attribute.getBaseValue() - amount);
            this.observers.forEach((key, value) -> value.get(MyStatsMaxHealthObserver.class).update());
        }
    }

    private void increaseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            this.player.getPlayer().setHealth(this.player.getPlayer().getHealth() + PlayerUtils.setMaxHealth(this.player.getPlayer(), attribute.getBaseValue() + amount));
            this.observers.forEach((key, value) -> value.get(MyStatsMaxHealthObserver.class).update());
        }
    }

    private void loseLives(int amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives()) {
            return;
        }

        //check if permission to bypass
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSELIVES.getPermissionString())) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLivesInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        //lose lives
        this.decreaseLives(amount);
    }

    private void loseLifeParts(int amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            return;
        }

        //check if permission to bypass
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSELIFEPARTS.getPermissionString())) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLifePartsInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        //lose life parts
        this.decreaseLifeParts(amount);
    }

    private void ban(PlayerDeathEvent event) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getDeathBanConfiguration().isUseDeathBan()) {
            return;
        }

        //Check if permission to bypass
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_BAN.getPermissionString())) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getDeathBanConfiguration().getDisableBanInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        DamageCause cause = EventUtils.getDamageCauseFromDamageEvent(this, damageEvent);
        if (cause == null) {
            return;
        }

        Killer killer = this.isReviving() ? this.getReviving() : EventUtils.getDamageEventKiller(damageEvent), tagger = this.getCombatTagger();
        if (killer != null && killer.equals(tagger)) {
            tagger = null;
        }

        Ban ban = BanUtils.getDeathBan(this.player.getPlayer(), this, cause, killer, tagger, event.getDeathMessage(), EventUtils.getDamageCauseTypeFromEntityDamageEvent(damageEvent));

        //check if not killed due to self harm if disabled
        if (!this.plugin.getConfigurations().getDeathBanConfiguration().isSelfHarmBan() && ban.getKiller() != null && ban.getKiller().getName().equals(this.player.getPlayer().getName())) {
            return;
        }

        //ban player
        if (ban.getBanTime() > 0) {
            this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> serverData.addBan(this.player.getUniqueId(), this.addBan(ban))).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

            if (this.plugin.getConfigurations().getDeathBanConfiguration().isLightningOnDeathBan() && !this.plugin.getConfigurations().getMiscellaneousConfiguration().isLightningOnDeath()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.player.getPlayer().getWorld().strikeLightningEffect(this.player.getPlayer().getLocation()));
            }

            if (!this.plugin.getConfigurations().getDeathBanConfiguration().getCommandsOnDeathBan().isEmpty() && this.player.getName() != null) {
                this.plugin.getConfigurations().getDeathBanConfiguration().getCommandsOnDeathBan().forEach(e -> Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), e.replaceAll("%player%", this.player.getName()))));
            }

            BanUtils.deathBan(this, ban);
        }
    }

    private Pair<Integer, Ban> addBan(Ban ban) {
        int key = (this.bans.isEmpty() ? 0 : this.bans.lastKey()) + 1;
        this.bans.put(key, ban);
        this.observers.forEach((k, value) -> value.get(MyStatsDeathBansObserver.class).update());
        return new Pair<>(key, ban);
    }

    public void onRespawn() {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() == null) {
            return;
        }

        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives()) {
            if (this.lives != 0) {
                return;
            }

            this.setLives(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAfterBan());
        }

        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAfterBan() != -1) {
            this.setLifeParts(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAfterBan());
        }

        if (this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth() && this.plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealthAfterBan() != -1) {
            PlayerUtils.setMaxHealth(this.player.getPlayer(), this.plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealthAfterBan());
            AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                this.player.getPlayer().setHealth(attribute.getBaseValue());
            }
        }

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    public void onEntityKill(EntityType type) {
        if (this.isSpectatorBanned()) {
            return;
        }

        //gain life parts on kill
        this.onLifePartsEntityKill(type);

        //gain max health on kill
        this.onMaxHealthEntityKill(type);

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    private void onLifePartsEntityKill(EntityType type) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isLifePartsOnKill()) {
            return;
        }

        //check if permission to gain life parts for kill
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_GAINLIFEPARTS_KILL.getPermissionString())) {
            return;
        }

        //gain life parts
        this.gainLifeParts(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsPerKill().getOrDefault(type, 0));
    }

    private void onMaxHealthEntityKill(EntityType type) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isMaxHealthIncreaseOnKill()) {
            return;
        }

        //check if permission to gain max health for kill
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_GAINMAXHEALTH_KILL.getPermissionString())) {
            return;
        }

        //increase max health
        this.gainMaxHealth(this.plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealthIncreasePerKill().getOrDefault(type, 0D));
    }

    private void gainLifeParts(int amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableGainingLifePartsInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        this.increaseLifeParts(amount);
    }

    private void gainMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableGainingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        this.increaseMaxHealth(amount);
    }

    public void onCombatTag(Killer tagger) {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isUseCombatTag()) {
            return;
        }

        //check if combat tag self enabled and if self harming
        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagSelf() && tagger.getName().equals(this.player.getName())) {
            return;
        }

        //check if in disabled worlds
        if (this.plugin.getConfigurations().getCombatTagConfiguration().getDisableCombatTagInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        if (!this.combatTag.isEmpty()) {
            this.combatTag.forEach(e -> e.restart(tagger));
        } else {
            this.combatTag = this.plugin.getMessages().getCombatTagNotificationsConfiguration(this.player.getPlayer(), this, tagger);
            this.combatTag.forEach(AbstractCombatTag::start);
        }
    }

    public void removeFromCombatTag(AbstractCombatTag tag) {
        this.combatTag.remove(tag);
    }

    public void unCombatTag() {
        if (!this.combatTag.isEmpty()) {
            new ArrayList<>(this.combatTag).forEach(AbstractCombatTag::stop);
        }
    }

    public void onJoin() {
        if (this.player.getPlayer() == null) {
            return;
        }

        //Get rid of death screen after death ban if disabled
        this.respawn();

        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            Pair<Integer, Ban> banPair = serverData.getBan(this.player);

            if (this.isSpectatorBanned() && this.player.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.player.getPlayer().setGameMode(GameMode.SPECTATOR));
            }

            if (banPair != null) {
                new BanExpiration(this, banPair.getValue1()).start();
                return;
            }

            if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
                AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute != null && attribute.getBaseValue() != 20D) {
                    this.player.getPlayer().setHealth(this.player.getPlayer().getHealth() + PlayerUtils.setMaxHealth(this.player.getPlayer(), 20D));
                }
            } else if (this.player.getPlayer().getHealth() != 0D) {
                //fix visual bug from Minecraft when max health is higher than 20
                this.player.getPlayer().setHealth(this.player.getPlayer().getHealth());
            }

            if (!this.playtime.isEmpty()) {
                this.playtime.forEach(AbstractPlaytime::stop);
                this.playtime.clear();
            }

            if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts() && this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime() && !this.isSpectatorBanned()) {
                this.playtime.add(new PlaytimeLifePart(this));
            }

            if (this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth() && this.plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime() && !this.isSpectatorBanned()) {
                this.playtime.add(new PlaytimeMaxHealth(this));
            }

            if (this.plugin.getConfigurations().getReviveConfiguration().isUseRevive() && this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives() > 0 && !this.isSpectatorBanned() && !this.player.getPlayer().hasPermission(Permission.BYPASS_REVIVECOOLDOWN.getPermissionString())) {
                this.playtime.add(new PlaytimeRevive(this));
            }

            this.playtime.forEach(AbstractPlaytime::start);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void decreaseTimeTillNextLifePart(int amount) {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            return;
        }

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime()) {
            return;
        }

        if (this.player.getPlayer().hasPermission(Permission.BYPASS_GAINLIFEPARTS_PLAYTIME.getPermissionString())) {
            return;
        }

        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableGainingLifePartsInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        if (this.lifeParts >= this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts()) {
            return;
        }

        long decreased = this.getTimeTillNextLifePart() - amount;
        if (decreased <= 0) {
            this.gainLifeParts(1);
            this.setTimeTillNextLifePart(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextLifePart(decreased);
        }
    }

    public void decreaseTimeTillNextMaxHealth(int amount) {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime()) {
            return;
        }

        if (this.player.getPlayer().hasPermission(Permission.BYPASS_GAINMAXHEALTH_PLAYTIME.getPermissionString())) {
            return;
        }

        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableGainingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            return;
        }

        AttributeInstance attributeInstance = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attributeInstance != null && attributeInstance.getBaseValue() >= this.plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()) {
            return;
        }

        long decreased = this.getTimeTillNextMaxHealth() - amount;
        if (decreased <= 0) {
            this.gainMaxHealth(1D);
            this.setTimeTillNextMaxHealth(this.plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextMaxHealth(decreased);
        }
    }

    public void decreaseTimeTillNextRevive(int amount) {
        if (this.isSpectatorBanned()) {
            return;
        }

        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
            return;
        }

        if (this.timeTillNextRevive == 0) {
            return;
        }

        this.setTimeTillNextRevive(this.getTimeTillNextRevive() - amount);
    }

    public long getTimeTillNextMaxHealth() {
        return this.timeTillNextMaxHealth;
    }

    private void setTimeTillNextMaxHealth(long timeTillNextMaxHealth) {
        this.timeTillNextMaxHealth = Math.max(0, Math.min(timeTillNextMaxHealth, this.plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart()));
        this.observers.forEach((key, value) -> value.get(MyStatsTimeTillNextMaxHealthObserver.class).update());
    }

    public void onLeave() {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (this.banExpiration != null) {
            this.banExpiration.stop();
        }

        this.stopPlaytimeRunnables();

        if (this.plugin.getConfigurations().getCombatTagConfiguration().isUseCombatTag()) {
            if (this.isCombatTagged() && !this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagPlayerKickDeath() && this.kicked) {
                this.unCombatTag();
                this.kicked = false;
            } else if (this.isCombatTagged()) {
                this.combatLogged = true;
                this.player.getPlayer().setHealth(0.0D);
            }
        }

        this.plugin.getPlayerRepository().updatePlayerData(this);
        this.plugin.getPlayerRepository().removeFromPlayerCache(player);
    }

    public void stopPlaytimeRunnables() {
        if (!this.playtime.isEmpty()) {
            this.playtime.forEach(AbstractPlaytime::stop);
            this.playtime.clear();
        }
    }

    public void onReviving(PlayerData revivingData) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.checkRevivePermissionsReviver(revivingData.getPlayer())) {
            return;
        }

        if (!this.checkRevivePermissionsReviving(revivingData)) {
            return;
        }

        if (revivingData.getLives() == 0) {
            if (!this.player.getPlayer().hasPermission(Permission.GAIN_REVIVE_DEATH.getPermissionString())) {
                this.player.getPlayer().sendMessage("§cYou don't have the right permission to give lives to a dead banned player.");
                return;
            }

            if (!BanUtils.unDeathBan(revivingData)) {
                this.player.getPlayer().sendMessage(String.format("%s is not death banned and cannot be unbanned by reviving.", revivingData.getPlayer().getName()));
                return;
            }
        } else {
            if (!this.player.getPlayer().hasPermission(Permission.GAIN_REVIVE_ALIVE.getPermissionString())) {
                this.player.getPlayer().sendMessage("§cYou don't have the right permission to give lives to an alive player.");
                return;
            }
        }
        revivingData.onRevive(this.player);

        this.setTimeTillNextRevive(this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());

        if (this.getLives() == 1) {
            this.reviving = new Killer(revivingData.getPlayer().getName(), revivingData.getPlayer().getPlayer() == null ? null : revivingData.getPlayer().getPlayer().getDisplayName(), EntityType.PLAYER);
            Bukkit.getScheduler().runTask(this.plugin, () -> this.player.getPlayer().setHealth(0.0D));
        } else {
            int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesLostOnReviving();
            this.decreaseLives(amount);
            this.player.getPlayer().sendMessage(String.format("§aSuccessfully given §e%s§a to §e%s§a, you have §e%s §aleft.", amount + (amount > 1 ? " lives" : " life"), revivingData.getPlayer().getName(), this.getLives() + (this.getLives() > 1 ? " lives" : " life")));
        }

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    public boolean checkRevivePermissionsReviving(PlayerData revivingData) {
        //check if player already on max lives
        if (revivingData.getLives() == this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives()) {
            if (this.player.getPlayer() != null) {
                this.player.getPlayer().sendMessage(String.format("§c%s already has the maximum amount of lives.", revivingData.getPlayer().getName()));
            }
            return false;
        }
        return true;
    }

    public boolean checkRevivePermissionsReviver(OfflinePlayer reviving) {
        if (this.player.getPlayer() == null) {
            return false;
        }

        if (this.isSpectatorBanned()) {
            this.player.getPlayer().sendMessage("§cYou cannot revive while spectator death banned.");
            return false;
        }

        //check if reviving is enabled
        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
            this.player.getPlayer().sendMessage("§cReviving is not enabled on the server.");
            return false;
        }

        //check if permission
        if (!this.player.getPlayer().hasPermission(Permission.REVIVE.getPermissionString())) {
            return false;
        }

        //check if not same player
        if (this.player.getPlayer().getUniqueId().equals(reviving.getUniqueId())) {
            this.player.getPlayer().sendMessage("§cYou cannot revive yourself, that would break the space-time continuum!");
            return false;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getReviveConfiguration().getDisableReviveInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase())) {
            this.player.getPlayer().sendMessage(String.format("§cYou cannot revive %s while in this world (%s).", reviving.getName(), this.player.getPlayer().getWorld().getName()));
            return false;
        }

        //check if enough lives left to revive
        int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesLostOnReviving();
        if (this.getLives() < amount) {
            this.player.getPlayer().sendMessage(String.format("§cYou'll need %s in order to revive %s, you currently have %s.", amount + (amount > 1 ? " lives" : " life"), reviving.getName(), this.getLives() + (this.getLives() > 1 ? " lives" : " life")));
            return false;
        }

        //check if revive on cooldown
        if (this.timeTillNextRevive > 0 && !this.player.getPlayer().hasPermission(Permission.BYPASS_REVIVECOOLDOWN.getPermissionString())) {
            this.player.getPlayer().sendMessage(String.format("§cYou cannot revive %s for another %s.", reviving.getName(), MessageUtils.getTimeFromTicks(this.getTimeTillNextRevive(), TimePattern.LONG)));
            return false;
        }

        return true;
    }

    public void onRevive(OfflinePlayer reviver) {
        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
            return;
        }

        int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesGainedOnRevive();
        this.increaseLives(amount);
        if (this.player.getPlayer() != null) {
            this.player.getPlayer().sendMessage(String.format("§a%s has successfully given you §e%s§a, you now have §e%s§a.", reviver.getName(), amount + (amount > 1 ? " lives" : " life"), this.getLives() + (this.getLives() > 1 ? " lives" : " life")));
        }

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    public long getTimeTillNextLifePart() {
        return timeTillNextLifePart;
    }

    private void setTimeTillNextLifePart(long timeTillNextLifePart) {
        this.timeTillNextLifePart = Math.max(0, Math.min(timeTillNextLifePart, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart()));
        this.observers.forEach((key, value) -> value.get(MyStatsTimeTillNextLifePartObserver.class).update());
    }

    public void onReload() {
        this.onJoin();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<Object, Object> cBans = new HashMap<>();
        this.bans.forEach((key, value) -> cBans.put(key, value.serialize()));

        map.put("Bans", cBans);
        map.put("TimeTillNextMaxHealth", this.timeTillNextMaxHealth);
        map.put("TimeTillNextLifePart", this.timeTillNextLifePart);
        map.put("SpectatorBanned", this.spectatorBanned);
        map.put("TimeTillNextRevive", this.timeTillNextRevive);
        map.put("LifeParts", this.lifeParts);
        map.put("Lives", this.lives);
        map.put("LastKnownIp", this.lastKnownIp);
        map.put("LastKnownName", this.player.getName());

        return map;
    }

    public void onKick() {
        this.kicked = true;
    }

    public boolean isReviving() {
        return this.reviving != null;
    }

    public Killer getReviving() {
        return reviving;
    }

    public Ban getLastDeathBan() {
        Map.Entry<Integer, Ban> entry = this.bans.lastEntry();
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    public void registerObserver(Player player, AbstractGui gui) {
        Map<Class<?>, IObserver> observers = new HashMap<>();
        if (gui instanceof GuiMyStats) {
            GuiMyStats guiMyStats = (GuiMyStats) gui;
            Arrays.asList(
                    new MyStatsDeathBansObserver(guiMyStats),
                    new MyStatsLifePartsObserver(guiMyStats),
                    new MyStatsLivesObserver(guiMyStats),
                    new MyStatsMaxHealthObserver(guiMyStats),
                    new MyStatsTimeTillNextLifePartObserver(guiMyStats),
                    new MyStatsTimeTillNextMaxHealthObserver(guiMyStats),
                    new MyStatsTimeTillNextReviveObserver(guiMyStats)
            ).forEach(e -> observers.put(e.getClass(), e));
        }
        if (!observers.isEmpty()) {
            this.observers.put(player.getUniqueId(), observers);
        }
    }

    public void unregisterObserver(Player player) {
        this.observers.remove(player.getUniqueId());
    }

    public BanExpiration getBanExpiration() {
        return banExpiration;
    }

    public void setBanExpiration(BanExpiration banExpiration) {
        this.banExpiration = banExpiration;
    }

    public boolean isSpectatorBanned() {
        return spectatorBanned;
    }

    public void setSpectatorBanned(boolean spectatorBanned) {
        this.spectatorBanned = spectatorBanned;
    }

    public void reset() {
        this.setLives(JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart());
        this.setLifeParts(JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        this.setTimeTillNextRevive(JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getReviveConfiguration().isReviveOnFirstJoin() ? 0L : JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getReviveConfiguration().getTimeBetweenRevives());
        this.setTimeTillNextLifePart(JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
        this.setTimeTillNextMaxHealth(JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart());
        this.bans.clear();
        BanUtils.unDeathBan(this);
        this.plugin.getPlayerRepository().deletePlayerData(this.player);
        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    public boolean isCombatLogged() {
        return combatLogged;
    }

    public void setCombatLogged(boolean combatLogged) {
        this.combatLogged = combatLogged;
    }

    public boolean isCombatTagged() {
        return this.combatTagger != null;
    }

    public Killer getCombatTagger() {
        return combatTagger;
    }

    public void setCombatTagger(Killer combatTagger) {
        this.combatTagger = combatTagger;
    }
}
