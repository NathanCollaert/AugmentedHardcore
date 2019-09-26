package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.LiteDeathBanConfig;
import com.backtobedrock.LiteDeathBan.runnables.CombatTagBossBarWarning;
import com.backtobedrock.LiteDeathBan.runnables.CombatTagChatWarning;
import com.backtobedrock.LiteDeathBan.helperClasses.DeathBanLogData;
import com.backtobedrock.LiteDeathBan.helperClasses.DeathLogData;
import com.backtobedrock.LiteDeathBan.runnables.CombatTagNoWarning;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

public class LiteDeathBanEventHandlers implements Listener {

    private final LiteDeathBan plugin;
    private final LiteDeathBanConfig config;
    private final ArrayList<UUID> kickList = new ArrayList<>();

    public LiteDeathBanEventHandlers(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.config = this.plugin.getLDBConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            e.setKickMessage(Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getPlayer().getName()).getReason());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(e.getPlayer(), this.plugin);
        if (config.isShowLivesInTabMenu()) {
            e.getPlayer().setPlayerListFooter(this.plugin.getMessages().getOnLivesLeftInTabMenu(e.getPlayer().getName(), crud.getLives(), this.config.getMaxLives()));
        }
        if (this.config.isGetPartOfLifeOnPlaytime()) {
            this.plugin.addToPlaytimeLastLifeOnlinePlayers(e.getPlayer().getUniqueId(), crud.getLastPartPlaytime());
            int playtimeParts = LiteDeathBanEventHandlers.checkPlaytimeForParts(e.getPlayer(), crud.getLastPartPlaytime(), this.config.getPlaytimePerPart(), this.config.getDisableGettingLifePartsInWorlds());
            if (playtimeParts > 0) {
                crud.setLifeParts(crud.getLifeParts() + playtimeParts, false);
                crud.setLastPartPlaytime(crud.getLastPartPlaytime() + (playtimeParts * this.config.getPlaytimePerPart() * 60 * 20), true);
                this.plugin.addToPlaytimeLastLifeOnlinePlayers(e.getPlayer().getUniqueId(), crud.getLastPartPlaytime());
            }
        }
        if (this.config.isUpdateChecker() && e.getPlayer().isOp() && this.plugin.isOldVersion()) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder("There is a new version of LiteDeathBan available ").color(ChatColor.YELLOW).append("here").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/litedeathban-an-advanced-deathban-plugin.71483/history")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go to spigotmc.org").create())).append(".").color(ChatColor.YELLOW).create());
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player plyr = ((Player) e.getEntity());
            if (e instanceof EntityDamageByEntityEvent) {
                Player dmgr = null;
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) e;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    dmgr = ((Player) lastEntityDamageEvent.getDamager());
                    this.tagPlayer(plyr, dmgr.getName());
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                dmgr = ((Player) a.getShooter());
                                this.tagPlayer(plyr, dmgr.getName());
                            }
                            break;
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                dmgr = ((Player) t.getShooter());
                                this.tagPlayer(plyr, dmgr.getName());
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (this.config.isGetPartOfLifeOnKill() && dmgr != null && !this.config.getDisableGettingLifePartsInWorlds().contains(dmgr.getWorld().getName().toLowerCase()) && plyr.getHealth() == 0D && dmgr != plyr) {
                    LiteDeathBanCRUD crud = new LiteDeathBanCRUD(dmgr, this.plugin);
                    crud.setLifeParts(crud.getLifeParts() + this.config.getPartsPerKill(), true);
                }
            }
            if (this.config.isDisableDyingInDisabledWorlds() && this.config.getDisableLosingLivesInWorlds().contains(plyr.getWorld().getName().toLowerCase()) && plyr.getHealth() - e.getFinalDamage() <= 0D) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (!this.config.isCombatTagPlayerKickDeath()) {
            this.kickList.add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player plyr = e.getPlayer();
        if (this.config.isCombatTag() && this.plugin.doesTagListContain(plyr.getUniqueId()) && !e.getPlayer().hasPermission("litedeathban.bypass.combattag") && !plyr.isBanned() && !this.kickList.contains(plyr.getUniqueId())) {
            plyr.setHealth(0.0D);
        }
        if (this.config.isGetPartOfLifeOnPlaytime()) {
            this.plugin.removeFromPlaytimeLastLifeOnlinePlayers(plyr.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        this.deathBan(e.getEntity());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(e.getPlayer(), this.plugin);
        if (crud.getLives() == 0) {
            crud.setLives(1, true);
        }
        String message = this.plugin.getMessages().getOnPlayerRespawn(e.getPlayer().getName(), crud.getLives(), this.config.getMaxLives());
        if (!message.trim().isEmpty() && !this.config.getDisableLosingLivesInWorlds().contains(e.getPlayer().getWorld().getName().toLowerCase()) && !e.getPlayer().hasPermission("litedeathban.bypass.loselives")) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder(message).create());
        }
    }

    private void tagPlayer(Player plyr, String taggedBy) {
        UUID plyrID = plyr.getUniqueId();
        if (!(!this.config.isCombatTagSelf() && plyr.getName().equals(taggedBy)) && plyr.getHealth() != 0D && !this.config.getDisableCombatTagInWorlds().contains(plyr.getWorld().getName().toLowerCase()) && !plyr.hasPermission("litedeathban.bypass.combattag")) {
            boolean containsTag = this.plugin.doesTagListContain(plyrID);
            switch (this.config.getCombatTagWarningStyle().toLowerCase()) {
                case "none":
                    if (containsTag) {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask noTask = new CombatTagNoWarning(this.plugin, plyr).runTaskLater(this.plugin, this.config.getCombatTagTime() * 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), noTask.getTaskId());
                    break;
                case "bossbar":
                    if (containsTag) {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask bossBarTask = new CombatTagBossBarWarning(this.plugin, this.config.getCombatTagTime(), plyr, taggedBy).runTaskTimer(this.plugin, 0, 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), bossBarTask.getTaskId());
                    break;

                case "chat":
                    if (!containsTag) {
                        plyr.spigot().sendMessage(new ComponentBuilder(this.plugin.getMessages().getOnCombatTaggedChat(plyr.getName(), taggedBy, this.config.getCombatTagTime())).create());
                    } else {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask chatTask = new CombatTagChatWarning(this.plugin, plyr, taggedBy).runTaskLater(this.plugin, this.config.getCombatTagTime() * 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), chatTask.getTaskId());
                    break;
            }
        }
    }

    private void deathBan(Player plyr) {
        LocalDateTime now = LocalDateTime.now();
        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
        if (this.config.isPartsLostUponDeath() && !this.config.getDisableLosingLifePartsInWorlds().contains(plyr.getWorld().getName().toLowerCase()) && !plyr.hasPermission("litedeathban.bypass.loseparts")) {
            crud.setLifeParts(0, false);
        }
        if (!this.config.getDisableLosingLivesInWorlds().contains(plyr.getWorld().getName().toLowerCase()) && !plyr.hasPermission("litedeathban.bypass.loselives")) {
            crud.setLives(crud.getLives() - 1, false);
        }
        if (crud.getLives() == 0 && !this.config.getDisableBanInWorlds().contains(plyr.getWorld().getName().toLowerCase()) && !plyr.hasPermission("litedeathban.bypass.ban")) {
            int bantime = this.getBanTime(plyr);
            switch (bantime) {
                case -1:
                    if (this.config.isLogDeaths() && !this.config.getDisableLoggingDeathsInWorlds().contains(plyr.getWorld().getName().toLowerCase())) {
                        DeathLogData deathLogData = new DeathLogData(this.plugin, plyr, this.config.getSaveDateFormat().format(now), 1);
                    }
                    break;
                default:
                    crud.setTotalDeathBans(crud.getTotalDeathBans() + 1, false);
                    String banMessage = this.plugin.getMessages().getOnPlayerDeathBan(plyr.getName(), bantime, this.config.getSaveDateFormat().format(now.plusMinutes(bantime)), crud.getLastBanDate(), crud.getTotalDeathBans());
                    Bukkit.getBanList(BanList.Type.NAME).addBan(plyr.getName(), banMessage, Timestamp.valueOf(now.plusMinutes(bantime)), "LiteDeathBan");
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        plyr.kickPlayer(banMessage);
                    });
                    crud.setLastBanDate(now, true);
                    if (this.config.isLogDeathBans() && !this.config.getDisableLoggingDeathBansInWorlds().contains(plyr.getWorld().getName().toLowerCase())) {
                        DeathBanLogData deathBanLogData = new DeathBanLogData(this.plugin, plyr, this.config.getSaveDateFormat().format(now), this.config.getSaveDateFormat().format(now.plusMinutes(bantime)), bantime);
                    } else if (this.config.isLogDeaths() && !this.config.getDisableLoggingDeathsInWorlds().contains(plyr.getWorld().getName().toLowerCase())) {
                        DeathLogData deathLogData = new DeathLogData(this.plugin, plyr, this.config.getSaveDateFormat().format(now), 0);
                    }
                    break;
            }
        } else {
            crud.saveConfig();
            if (this.config.isLogDeaths() && !this.config.getDisableLoggingDeathsInWorlds().contains(plyr.getWorld().getName().toLowerCase())) {
                DeathLogData deathLogData = new DeathLogData(this.plugin, plyr, this.config.getSaveDateFormat().format(now), crud.getLives() == 0 ? 1 : crud.getLives());
            }
        }

        if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
            this.plugin.removeFromTagList(plyr.getUniqueId());
        }
    }

    private int getBanTime(Player plyr) {
        if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
            int bantime;
            if (this.config.isBantimeByPlaytime() && this.config.isBantimeByPlaytimeSinceLastDeath()) {
                int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.TIME_SINCE_DEATH) / 20 / 60) / this.config.getBantimeByPlaytimeInterval();
                bantime = this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumPlayerDeath(), amountOfIntervalsPassed, this.config.getPlayerDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
            } else if (this.config.isBantimeByPlaytime()) {
                int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60) / this.config.getBantimeByPlaytimeInterval();
                bantime = this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumPlayerDeath(), amountOfIntervalsPassed, this.config.getPlayerDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
            } else {
                bantime = this.config.getPlayerDeathBantime();
            }
            if (this.plugin.doesUsedReviveContain(plyr.getUniqueId())) {
                this.plugin.removeFromUsedRevive(plyr.getUniqueId());
                return bantime > this.config.getBantimeOnReviveDeath() ? bantime : this.config.getBantimeOnReviveDeath();
            } else {
                return bantime;
            }
        }
        if (this.config.isBantimeByPlaytime() && this.config.isBantimeByPlaytimeSinceLastDeath()) {
            int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.TIME_SINCE_DEATH) / 20 / 60) / this.config.getBantimeByPlaytimeInterval();
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumMonsterDeath(), amountOfIntervalsPassed, this.config.getMonsterDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
                case "ENVIRONMENT":
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumEnvironmentDeath(), amountOfIntervalsPassed, this.config.getEnvironmentDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
                default:
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumPlayerDeath(), amountOfIntervalsPassed, this.config.getPlayerDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
            }
        } else if (this.config.isBantimeByPlaytime()) {
            int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60) / this.config.getBantimeByPlaytimeInterval();
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumMonsterDeath(), amountOfIntervalsPassed, this.config.getMonsterDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
                case "ENVIRONMENT":
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumEnvironmentDeath(), amountOfIntervalsPassed, this.config.getEnvironmentDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
                default:
                    return this.linearVsExponentialBantime(this.config.getBantimeByPlaytimeMinimumPlayerDeath(), amountOfIntervalsPassed, this.config.getPlayerDeathBantime(), this.config.getBantimeByPlaytimeGrowth(), this.config.getBantimeByPlaytimePercent());
            }
        } else {
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.config.getMonsterDeathBantime();
                case "ENVIRONMENT":
                    return this.config.getEnvironmentDeathBantime();
                default:
                    return this.config.getPlayerDeathBantime();
            }
        }
    }

    private String getDeathCause(Player plyr) {
        EntityDamageEvent lastDamageCause = plyr.getLastDamageCause();
        if (lastDamageCause != null) {
            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) lastDamageCause;
                if (lastEntityDamageEvent.getDamager() instanceof Monster) {
                    return "MONSTER";
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Monster) {
                                return "MONSTER";
                            } else {
                                return "ENVIRONMENT";
                            }
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Monster) {
                                return "MONSTER";
                            } else {
                                return "ENVIRONMENT";
                            }
                        default:
                            return "MONSTER";
                    }
                }
            } else {
                return "ENVIRONMENT";
            }
        } else {
//            log.severe("[LiteDeathBan] Something went wrong, please contact the plugin author with the following code: 404. And tell him what you did.");
            return "OTHER";
        }
    }

    private int linearVsExponentialBantime(int min, int intervalsPassed, int max, String growth, int percent) {
        switch (growth) {
            case "linear":
                int bantimeLinear;
                try {
                    bantimeLinear = min * (percent * intervalsPassed / 100 + 1);
                } catch (Exception e) {
                    bantimeLinear = Integer.MAX_VALUE;
                }
                switch (max) {
                    case 0:
                        return bantimeLinear;
                    case -1:
                        return max;
                    default:
                        return bantimeLinear > max ? max : bantimeLinear;
                }
            case "exponential":
                int bantimeExponential;
                try {
                    bantimeExponential = (int) (min * Math.pow(((double) percent / 100 + 1), intervalsPassed));
                } catch (Exception e) {
                    bantimeExponential = Integer.MAX_VALUE;
                }
                switch (max) {
                    case 0:
                        return bantimeExponential;
                    case -1:
                        return max;
                    default:
                        return bantimeExponential > max ? max : bantimeExponential;
                }
            default:
                return -1;
        }
    }

    public static int checkPlaytimeForParts(Player plyr, long lastPartPlaytime, int playtimePerPart, List<String> disableGettingLifePartsInWorlds) {
        if (!disableGettingLifePartsInWorlds.contains(plyr.getWorld().getName().toLowerCase())) {
            long playtimeNow = plyr.getStatistic(Statistic.PLAY_ONE_MINUTE);
            int partsReceiving = (int) Math.floor(((playtimeNow - lastPartPlaytime) / 20 / 60) / playtimePerPart);
            return partsReceiving;
        } else {
            return 0;
        }
    }
}
