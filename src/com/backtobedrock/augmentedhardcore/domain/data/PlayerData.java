package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.runnables.CombatTag.AbstractCombatTag;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.AbstractPlaytime;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.LifePartPlaytime;
import com.backtobedrock.augmentedhardcore.runnables.Playtime.MaxHealthPlaytime;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.EventUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerData {
    //misc
    private final AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    private final OfflinePlayer player;
    //helpers
    private final List<AbstractPlaytime> playtime = new ArrayList<>();
    private List<AbstractCombatTag> combatTag = new ArrayList<>();
    private boolean kicked = false;
    private boolean combatLogged = false;
    private Killer reviving = null;
    //serializable
    private String lastKnownName;
    private int timeTillNextLifePart;
    private int timeTillNextMaxHealth;
    private long reviveCooldown;
    private int lifeParts;
    private int lives;
    private String ip;
    private final Map<Integer, Ban> bans;

    public PlayerData(OfflinePlayer player) {
        this(player, new LinkedHashMap<>(), JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart(), JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart(), JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart(), JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart(), -1L);
    }

    public PlayerData(OfflinePlayer player, Map<Integer, Ban> bans, int lives, int lifeParts, int timeTillNextLifePart, int timeTillNextMaxHealth, long reviveCooldown) {
        //serializable
        this.player = player;
        this.bans = bans;
        this.setTimeTillNextLifePart(timeTillNextLifePart);
        this.setTimeTillNextMaxHealth(timeTillNextMaxHealth);
        this.setLives(lives);
        this.setLifeParts(lifeParts);
        if (player.getPlayer() != null) {
            this.lastKnownName = player.getName();
            if (player.getPlayer().getAddress() != null) {
                InetSocketAddress address = player.getPlayer().getAddress();
                this.ip = address.getHostName() == null ? address.getHostString() : address.getHostName();
            }
            reviveCooldown = this.checkReviveCooldown(reviveCooldown);
        }
        this.setReviveCooldown(reviveCooldown);
    }

    public static PlayerData deserialize(ConfigurationSection section, OfflinePlayer player) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        Map<Integer, Ban> cBans = new LinkedHashMap<>();
        int cLives = section.getInt("Lives", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart());
        int cLifeParts = section.getInt("LifeParts", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        int cTimeTillNextLifePart = section.getInt("TimeTillNextLifePart", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
        int cTimeTillNextMaxHealth = section.getInt("TimeTillNextMaxHealth", plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart());
        long cReviveCooldown = section.getLong("ReviveCooldown", -1L);

        //get all bans
        ConfigurationSection bansSection = section.getConfigurationSection("Bans");
        if (bansSection != null) {
            int i = 1;
            for (String e : bansSection.getKeys(false)) {
                ConfigurationSection banSection = bansSection.getConfigurationSection(e);
                if (banSection != null) {
                    cBans.put(i, Ban.Deserialize(banSection));
                    i++;
                }
            }
        }

        return new PlayerData(player, cBans, cLives, cLifeParts, cTimeTillNextLifePart, cTimeTillNextMaxHealth, cReviveCooldown);
    }

    public long checkReviveCooldown(long reviveCooldown) {
        if (this.plugin.getConfigurations().getReviveConfiguration().isUseRevive() && reviveCooldown == -1L) {
            GregorianCalendar cal = new GregorianCalendar();
            if (!this.plugin.getConfigurations().getReviveConfiguration().isReviveOnFirstJoin()) {
                cal.add(Calendar.MINUTE, this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());
            }
            return cal.getTime().getTime();
        }
        return reviveCooldown;
    }

    private void setReviveCooldown(long reviveCooldown) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());
        this.reviveCooldown = Math.min(reviveCooldown, cal.getTime().getTime());
    }

    public String getIp() {
        return ip;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(Math.min(lives, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives()), 0);
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
    }

    public Map<Integer, Ban> getBans() {
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
        this.unCombatTag();

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

        this.plugin.getPlayerRepository().updatePlayerData(this);
    }

    public Killer getCombatTagger() {
        if (!this.combatTag.isEmpty()) {
            return this.combatTag.get(0).getTagger();
        }
        return null;
    }

    private void loseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            return;
        }

        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSEMAXHEALTH.getPermissionString()))
            return;

        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableLosingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

        this.decreaseMaxHealth(amount);
    }

    private void decreaseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            PlayerUtils.setMaxHealth(this.player.getPlayer(), attribute.getBaseValue() - amount);
        }
    }

    private void increaseMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            this.player.getPlayer().setHealth(this.player.getPlayer().getHealth() + PlayerUtils.setMaxHealth(this.player.getPlayer(), attribute.getBaseValue() + amount));
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
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSELIVES.getPermissionString()))
            return;

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLivesInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

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
        if (this.player.getPlayer().hasPermission(Permission.BYPASS_LOSELIFEPARTS.getPermissionString()))
            return;

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLifePartsInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

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
            BanUtils.deathBan(this, ban);
            this.plugin.getServerRepository().getServerData().thenAcceptAsync(serverData -> {
                //add ban to player and server data
                this.addBan(ban);
                serverData.addBan(this.player.getPlayer(), ban);
            });
        }
    }

    private void addBan(Ban ban) {
        this.bans.put(this.bans.size(), ban);
    }

    public void onRespawn() {
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
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableGainingLifePartsInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

        this.increaseLifeParts(amount);
    }

    private void gainMaxHealth(double amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableGainingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

        this.increaseMaxHealth(amount);
    }

    public void onCombatTag(Killer tagger) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isUseCombatTag()) {
            return;
        }

        //check if combat tag self enabled and if self harming
        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagSelf() && tagger.getName().equals(player.getName()))
            return;

        //check if in disabled worlds
        if (this.plugin.getConfigurations().getCombatTagConfiguration().getDisableCombatTagInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

        if (!this.combatTag.isEmpty()) {
            new ArrayList<>(this.combatTag).forEach(e -> e.restart(tagger));
        } else {
            this.combatTag = this.plugin.getMessages().getCombatTagNotificationsConfiguration(this.player.getPlayer(), this, tagger);
            new ArrayList<>(this.combatTag).forEach(AbstractCombatTag::start);
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

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            AttributeInstance attribute = this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null && attribute.getBaseValue() != 20D) {
                this.player.getPlayer().setHealth(this.player.getPlayer().getHealth() + PlayerUtils.setMaxHealth(this.player.getPlayer(), 20D));
            }
        } else {
            //fix visual bug from Minecraft when max health is higher than 20
            this.player.getPlayer().setHealth(this.player.getPlayer().getHealth());
        }

        if (!this.playtime.isEmpty()) {
            this.playtime.forEach(AbstractPlaytime::stop);
            this.playtime.clear();
        }

        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts() && this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime()) {
            this.playtime.add(new LifePartPlaytime(this));
        }
        if (this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth() && this.plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime()) {
            this.playtime.add(new MaxHealthPlaytime(this));
        }

        this.playtime.forEach(AbstractPlaytime::start);
    }

    public void decreaseTimeTillNextLifePart(int amount) {
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

        int decreased = this.getTimeTillNextLifePart() - amount;
        if (decreased <= 0) {
            this.gainLifeParts(1);
            this.setTimeTillNextLifePart(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextLifePart(decreased);
        }
    }

    public void decreaseTimeTillNextMaxHealth(int amount) {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            return;
        }

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime()) {
            return;
        }

        if (this.player.getPlayer().hasPermission(Permission.BYPASS_GAINMAXHEALTH_PLAYTIME.getPermissionString()))
            return;

        if (this.plugin.getConfigurations().getMaxHealthConfiguration().getDisableGainingMaxHealthInWorlds().contains(this.player.getPlayer().getWorld().getName().toLowerCase()))
            return;

        int decreased = this.getTimeTillNextMaxHealth() - amount;
        if (decreased <= 0) {
            this.gainMaxHealth(1D);
            this.setTimeTillNextMaxHealth(this.plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextMaxHealth(decreased);
        }
    }

    public int getTimeTillNextMaxHealth() {
        return this.timeTillNextMaxHealth;
    }

    private void setTimeTillNextMaxHealth(int timeTillNextMaxHealth) {
        this.timeTillNextMaxHealth = Math.min(timeTillNextMaxHealth, this.plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart());
    }

    public void onLeave() {
        if (this.player.getPlayer() == null) {
            return;
        }

        if (!this.playtime.isEmpty()) {
            this.playtime.forEach(AbstractPlaytime::stop);
            this.playtime.clear();
        }

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

        this.setReviveCooldown(Long.MAX_VALUE);

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
        if (this.isReviveOnCooldown()) {
            this.player.getPlayer().sendMessage(String.format("§cYou cannot revive %s for another %s.", reviving.getName(), MessageUtils.getTimeFromTicks(this.getReviveCooldownLeftInTicks(), false, true)));
            return false;
        }

        return true;
    }

    private boolean isReviveOnCooldown() {
        return this.getReviveCooldownLeftInTicks() > 0;
    }

    public long getReviveCooldownLeftInTicks() {
        return Math.max(MessageUtils.timeUnitToTicks(this.reviveCooldown - new Date().getTime(), TimeUnit.MILLISECONDS), 0L);
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

    public int getTimeTillNextLifePart() {
        return timeTillNextLifePart;
    }

    private void setTimeTillNextLifePart(int timeTillNextLifePart) {
        this.timeTillNextLifePart = Math.min(timeTillNextLifePart, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
    }

    public void onReload() {
        this.onJoin();
        this.setReviveCooldown(this.checkReviveCooldown(this.reviveCooldown));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<Object, Object> cBans = new HashMap<>();
        this.bans.forEach((key, value) -> cBans.put(key, value.serialize()));

        map.put("Bans", cBans);
        map.put("TimeTillNextLifePart", this.timeTillNextLifePart);
        map.put("TimeTillNextMaxHealth", this.timeTillNextMaxHealth);
        map.put("LifeParts", this.lifeParts);
        map.put("Lives", this.lives);
        map.put("LastKnownPlayername", this.lastKnownName);
        map.put("ReviveCooldown", this.reviveCooldown);
        map.put("LastKnownIP", this.ip);

        return map;
    }

    public boolean isCombatTagged() {
        return !this.combatTag.isEmpty();
    }

    public void onKick() {
        this.kicked = true;
    }

    public boolean isCombatLogged() {
        return combatLogged;
    }

    public boolean isReviving() {
        return this.reviving != null;
    }

    public Killer getReviving() {
        return reviving;
    }

    public Ban getLastDeathBan() {
        return this.bans.get(this.bans.size());
    }
}
