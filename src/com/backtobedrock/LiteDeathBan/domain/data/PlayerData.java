package com.backtobedrock.LiteDeathBan.domain.data;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.Ban;
import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.enums.Permission;
import com.backtobedrock.LiteDeathBan.runnables.CombatTag.AbstractCombatTag;
import com.backtobedrock.LiteDeathBan.runnables.Playtime;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import com.backtobedrock.LiteDeathBan.utils.MessageUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.util.*;

public class PlayerData {
    //misc
    private final LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    //serializable
    private final Map<Integer, Ban> bans;
    private final String lastKnownName;
    private long reviveCooldown;
    //helpers
    private List<AbstractCombatTag> combatTag = new ArrayList<>();
    private Playtime playtimeRunnable;
    private boolean kicked = false;
    private boolean combatLogged = false;
    private Killer reviving = null;
    private int timeTillNextLifePart;
    private int lifeParts;
    private int lives;
    private String ip;

    public PlayerData(OfflinePlayer player) {
        //serializable
        this.bans = new LinkedHashMap<>();
        this.lastKnownName = player.getName();
        this.timeTillNextLifePart = this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart();
        this.setLives(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart());
        this.setLifeParts(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        if (this.plugin.getConfigurations().getReviveConfiguration().isReviveOnFirstJoin()) {
            this.reviveCooldown = new Date().getTime();
        } else {
            GregorianCalendar cal = new GregorianCalendar();
            cal.add(Calendar.MINUTE, this.plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());
            this.reviveCooldown = cal.getTime().getTime();
        }
        if (player.getPlayer() != null && player.getPlayer().getAddress() != null) {
            InetSocketAddress address = player.getPlayer().getAddress();
            this.ip = address.getHostName() == null ? address.getHostString() : address.getHostName();
        }
    }

    public PlayerData(OfflinePlayer player, Map<Integer, Ban> bans, int lives, int lifeParts, int timeTillNextLifePart, long reviveCooldown) {
        //serializable
        this.bans = bans;
        this.lastKnownName = player.getName();
        this.timeTillNextLifePart = timeTillNextLifePart;
        this.setLives(player, lives);
        this.setLifeParts(player, lifeParts);
        this.setReviveCooldown(reviveCooldown);
        if (player.getPlayer() != null && player.getPlayer().getAddress() != null) {
            InetSocketAddress address = player.getPlayer().getAddress();
            this.ip = address.getHostName() == null ? address.getHostString() : address.getHostName();
        }
    }

    public void setReviveCooldown(long reviveCooldown) {
        this.reviveCooldown = reviveCooldown;
    }

    public static PlayerData deserialize(ConfigurationSection section, OfflinePlayer player) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        Map<Integer, Ban> cBans = new LinkedHashMap<>();
        int cLives = section.getInt("Lives", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart());
        int cLifeParts = section.getInt("LifeParts", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        int cTimeTillNextLifePart = section.getInt("TimeTillNextLifePart", plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
        long cReviveCooldown = section.getLong("ReviveCooldown", -1L);

        //get all bans
        ConfigurationSection bansSection = section.getConfigurationSection("Bans");
        if (bansSection != null) {
            int i = 0;
            for (String e : bansSection.getKeys(false)) {
                ConfigurationSection banSection = bansSection.getConfigurationSection(e);
                if (banSection != null) {
                    Ban ban = Ban.Deserialize(banSection, player.getUniqueId());
                    if (ban != null) {
                        cBans.put(i, ban);
                        i++;
                    }
                }
            }
        }

        //get revive cooldown if not in data
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives());
        long maxDate = cal.getTime().getTime();
        if (cReviveCooldown == -1L) {
            if (plugin.getConfigurations().getReviveConfiguration().isReviveOnFirstJoin()) {
                cReviveCooldown = new Date().getTime();
            } else {
                cReviveCooldown = maxDate;
            }
        } else {
            cReviveCooldown = Math.min(maxDate, cReviveCooldown);
        }

        return new PlayerData(player, cBans, cLives, cLifeParts, cTimeTillNextLifePart, cReviveCooldown);
    }

    public String getIp() {
        return ip;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(OfflinePlayer player, int lives) {
        this.lives = Math.max(Math.min(lives, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives()), 0);
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public int getLifeParts() {
        return lifeParts;
    }

    public void setLifeParts(OfflinePlayer player, int lifeParts) {
        int lifePartsPerLife = this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsPerLife();
        int lives = Math.min(lifeParts / lifePartsPerLife, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives() - this.getLives());
        if (lives > 0)
            this.increaseLives(player, lives);
        this.lifeParts = Math.min(this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts(), Math.max(0, lifeParts - (lives * lifePartsPerLife)));
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public Map<Integer, Ban> getBans() {
        return bans;
    }

    public int getBanCount() {
        return this.bans.size();
    }

    private void decreaseLives(OfflinePlayer player, int amount) {
        this.setLives(player, this.getLives() - amount);
    }

    public void increaseLives(OfflinePlayer player, int amount) {
        this.setLives(player, this.getLives() + amount);
    }

    private void decreaseLifeParts(OfflinePlayer player, int amount) {
        this.setLifeParts(player, this.getLifeParts() - amount);
    }

    public void increaseLifeParts(OfflinePlayer player, int amount) {
        this.setLifeParts(player, this.getLifeParts() + amount);
    }

    public void onDeath(Player player, Ban ban) {
        //remove combat tag
        this.unCombatTag();

        //lose lives
        this.loseLives(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesLostPerDeath());

        //lose life parts
        if (this.lives != 0) {
            this.loseLifeParts(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeath());
        } else {
            this.loseLifeParts(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeathBan());
        }

        //ban if 0 lives
        if (this.lives == 0)
            this.ban(player, ban);
    }

    public Killer getCombatTagger() {
        if (!this.combatTag.isEmpty())
            return this.combatTag.get(0).getTagger();
        return null;
    }

    private void loseLives(Player player, int amount) {
        //check if permission to bypass
        if (player.hasPermission(Permission.BYPASS_LOSELIVES.getPermissionString()))
            return;

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLivesInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //lose lives
        this.decreaseLives(player, amount);
    }

    private void loseLifeParts(Player player, int amount) {
        //check if permission to bypass
        if (player.hasPermission(Permission.BYPASS_LOSELIFEPARTS.getPermissionString()))
            return;

        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableLosingLifePartsInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //lose life parts
        this.decreaseLifeParts(player, amount);
    }

    private void ban(Player player, Ban ban) {
        //Check if permission to bypass
        if (player.hasPermission(Permission.BYPASS_BAN.getPermissionString()))
            return;

        //check if in disabled world
        if (this.plugin.getConfigurations().getBanTimesConfiguration().getDisableBanInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //check if not killed due to self harm if disabled
        if (!this.plugin.getConfigurations().getBanTimesConfiguration().isSelfHarmBan() && ban.getKiller().getName().equals(player.getName()))
            return;

        //ban player
        if (ban.getBanTime() > 0) {
            ban.deathBan(this, player);
            //add ban to player and server data
            this.addBan(player, ban);
            this.plugin.getServerRepository().getServerData(data -> {
                data.addBan(player, ban);
            });
        }
    }

    public void addBan(OfflinePlayer player, Ban ban) {
        this.bans.put(this.bans.size(), ban);
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public void onRespawn(Player player) {
        //check if player has 0 lives
        if (this.lives > 0)
            return;

        //give player lives
        this.increaseLives(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAfterBan());

        //give player life parts
        this.increaseLifeParts(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAfterBan());
    }

    public void onEntityKill(Player player, EntityType type) {
        //check if enabled
        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isLifePartsOnKill())
            return;

        //check if permission to gain life parts for kill
        if (player.hasPermission(Permission.BYPASS_GAINLIFEPARTS_KILL.getPermissionString()))
            return;

        //gain life parts
        this.gainLifeParts(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsPerKill().getOrDefault(type, 0));
    }

    private void gainLifeParts(Player player, int amount) {
        //check if in disabled world
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getDisableGainingLifePartsInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        this.increaseLifeParts(player, amount);
    }

    public boolean isBanned(OfflinePlayer player) {
        BanList.Type type = this.getIp() == null ? BanList.Type.NAME : this.plugin.getConfigurations().getBanTimesConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        return banList.isBanned(BanUtils.getBanParameter(this, player, type));
    }

    public void onCombatTag(Player player, Killer tagger) {
        //check if combat tag self enabled and if self harming
        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagSelf() && tagger.getName().equals(player.getName())) {
            return;
        }

        //check if in disabled worlds
        if (this.plugin.getConfigurations().getCombatTagConfiguration().getDisableCombatTagInWorlds().contains(player.getLocation().getWorld().getName())) {
            return;
        }

        if (!this.combatTag.isEmpty()) {
            new ArrayList<>(this.combatTag).forEach(e -> e.restart(tagger));
        } else {
            //TODO: get notifications
            this.combatTag = this.plugin.getConfigurations().getCombatTagConfiguration().getNotificationConfiguration(player, this, tagger);
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

    public void onJoin(Player player) {
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime()) {
            if (this.timeTillNextLifePart > plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart())
                this.setTimeTillNextLifePart(player, plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());

            if (this.playtimeRunnable != null)
                return;

            this.playtimeRunnable = new Playtime(player);
            this.playtimeRunnable.start();
        }
    }

    public void decreaseTimeTillNextLifePart(Player player, int amount) {
        //check if permission to gain life parts for playtime
        if (player.hasPermission(Permission.BYPASS_GAINLIFEPARTS_PLAYTIME.getPermissionString()))
            return;

        int decreased = this.getTimeTillNextLifePart() - amount;
        if (decreased <= 0) {
            this.gainLifeParts(player, 1);
            this.setTimeTillNextLifePart(player, this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextLifePart(player, decreased);
        }
    }

    public void onLeave(Player player) {
        if (this.playtimeRunnable != null) {
            this.playtimeRunnable.stop();
            this.playtimeRunnable = null;
        }

        if (this.isCombatTagged()) {
            if (!this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagPlayerKickDeath() && this.kicked) {
                this.unCombatTag();
                return;
            }
            this.combatLogged = true;
            player.setHealth(0.0D);
        }

        this.plugin.getPlayerRepository().removeFromPlayerCache(player);
    }

    public void onReviving(Player reviver, OfflinePlayer revivingPlayer) {
        if (!this.checkRevivePermissions(reviver, revivingPlayer))
            return;

        this.plugin.getPlayerRepository().getByPlayer(revivingPlayer, data -> {
            switch (data.getLives()) {
                case 0:
                    //check if able to revive if deathbanned
                    if (!reviver.hasPermission(Permission.GAIN_REVIVE_DEATH.getPermissionString())) {
                        reviver.sendMessage("§cYou don't have the right permission to give lives to a dead player.");
                        return;
                    }
                    if (!this.unDeathBan(reviver, revivingPlayer, true))
                        return;
                    data.onRevive(revivingPlayer, reviver);
                    break;
                default:
                    //check if able to revive if alive
                    if (!reviver.hasPermission(Permission.GAIN_REVIVE_ALIVE.getPermissionString())) {
                        reviver.sendMessage("§cYou don't have the right permission to give lives to an alive player.");
                        return;
                    }
                    data.onRevive(revivingPlayer, reviver);
                    break;
            }

            int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesLostOnReviving();
            this.decreaseLives(reviver, amount);
            if (this.getLives() == 0) {
                //deathban
                this.reviving = new Killer(revivingPlayer.getName(), revivingPlayer.getPlayer() == null ? null : revivingPlayer.getPlayer().getDisplayName(), EntityType.PLAYER);
                reviver.setHealth(0.0D);
            } else {
                reviver.sendMessage(String.format("§aSuccessfully given §e%s§a to §e%s§a, you have §e%s §aleft.", amount + (amount > 1 ? " lives" : " life"), revivingPlayer.getName(), this.getLives() + this.getLives() > 1 ? " lives" : " life"));
            }
        });
    }

    public boolean checkRevivePermissions(Player reviver, OfflinePlayer revivingPlayer) {
        //check if reviving is enabled
        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
            reviver.sendMessage("§cReviving is not enabled on the server.");
            return false;
        }

        //check if permission
        if (!reviver.hasPermission(Permission.REVIVE.getPermissionString()))
            return false;

        //check if not same player
        //TODO: uncomment
//        if (reviver.getUniqueId().equals(revivingPlayer.getUniqueId())) {
//            reviver.sendMessage("§cYou cannot revive yourself, that would break the space-time continuum!");
//            return false;
//        }

        //check if in disabled world
        if (this.plugin.getConfigurations().getReviveConfiguration().getDisableReviveInWorlds().contains(reviver.getWorld().getName().toLowerCase())) {
            reviver.sendMessage(String.format("§cYou cannot revive %s while in this world (%s).", revivingPlayer.getName(), reviver.getWorld().getName()));
            return false;
        }

        //check if enough lives left to revive
        int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesLostOnReviving();
        if (this.getLives() < amount) {
            reviver.sendMessage(String.format("§cYou'll need %s in order to revive %s, you currently have %s.", amount + (amount > 1 ? " lives" : " life"), revivingPlayer.getName(), this.getLives() + (this.getLives() > 1 ? " lives" : " life")));
            return false;
        }

        //check if revive on cooldown
        if (this.isReviveOnCooldown()) {
            reviver.sendMessage(String.format("§cYou cannot revive %s for another %s.", revivingPlayer.getName(), MessageUtils.getTimeFromTicks(this.getReviveCooldownLeftInTicks(), false, true)));
            return false;
        }

        return true;
    }

    private boolean isReviveOnCooldown() {
        return this.getReviveCooldownLeftInTicks() > 0;
    }

    private int getReviveCooldownLeftInTicks() {
        return Math.max((int) Math.ceil((double) (this.reviveCooldown - new Date().getTime()) / 50), 0);
    }

    public void onRevive(OfflinePlayer reviving, Player reviver) {
        int amount = this.plugin.getConfigurations().getReviveConfiguration().getLivesGainedOnRevive();
        this.increaseLives(reviving, amount);
        if (reviving.isOnline() && reviving.getPlayer() != null)
            reviving.getPlayer().sendMessage(String.format("§a%s has successfully given you §e%s§a, you now have §e%s§a.", reviver.getName(), amount + (amount > 1 ? " lives" : " life"), this.getLives() + (this.getLives() > 1 ? " lives" : " life")));
    }

    public int getTimeTillNextLifePart() {
        return timeTillNextLifePart;
    }

    public void setTimeTillNextLifePart(OfflinePlayer player, int timeTillNextLifePart) {
        this.timeTillNextLifePart = timeTillNextLifePart;
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public void onReload(Player player) {
        this.onJoin(player);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> cBans = new HashMap<>();
        this.bans.forEach((key, value) -> cBans.put(key.toString(), value.serialize()));

        map.put("Bans", cBans);
        map.put("TimeTillNextLifePart", this.timeTillNextLifePart);
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
        return reviving != null;
    }

    public Killer getReviving() {
        return reviving;
    }

    public boolean unDeathBan(CommandSender sender, OfflinePlayer player, boolean isRevive) {
        if (!this.isBanned(player)) {
            sender.sendMessage(String.format("§c%s is not banned.", player.getName()));
            return false;
        }

        ServerData data = this.plugin.getServerRepository().getServerDataSync();
        if (!data.isDeathBanned(player)) {
            sender.sendMessage(isRevive ? String.format("§c%s is not death banned and cannot be revived.", player.getName()) : String.format("§c%s is not death banned and cannot be unbanned by LiteDeathBan.", player.getName()));
            return false;
        }

        BanUtils.unDeathBan(player);
        return true;
    }
}