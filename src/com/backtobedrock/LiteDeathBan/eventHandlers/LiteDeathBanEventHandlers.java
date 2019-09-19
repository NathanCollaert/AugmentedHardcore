package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.helperClasses.CombatLogBossBarWarning;
import com.backtobedrock.LiteDeathBan.helperClasses.CombatLogChatWarning;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
    private static final Logger log = Bukkit.getLogger();
    private final ArrayList<UUID> kickList = new ArrayList<>();

    private final DateTimeFormatter saveDateFormat;
    private final String bantimeByPlaytimeGrowth;
    private final String combatLogWarningStyle;
    private final boolean combatLog;
    private final boolean bantimeByPlaytime;
    private final boolean bantimeByPlaytimeSinceLastDeath;
    private final boolean combatTagPlayerKickDeath;
    private final int playerDeathBantime;
    private final int monsterDeathBantime;
    private final int environmentDeathBantime;
    private final int bantimeByPlaytimeInterval;
    private final int bantimeByPlaytimeMinimumPlayerDeath;
    private final int bantimeByPlaytimeMinimumMonsterDeath;
    private final int bantimeByPlaytimeMinimumEnvironmentDeath;
    private final int combatLogTime;
    private final int bantimeByPlaytimePercent;
    private final int bantimeOnReviveDeath;

    public LiteDeathBanEventHandlers(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.combatLog = this.plugin.getLDBConfig().isCombatTag();
        this.playerDeathBantime = this.plugin.getLDBConfig().getPlayerDeathBantime();
        this.monsterDeathBantime = this.plugin.getLDBConfig().getMonsterDeathBantime();
        this.environmentDeathBantime = this.plugin.getLDBConfig().getEnvironmentDeathBantime();
        this.bantimeByPlaytimeInterval = this.plugin.getLDBConfig().getBantimeByPlaytimeInterval();
        this.bantimeByPlaytimeMinimumPlayerDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumPlayerDeath();
        this.bantimeByPlaytimeMinimumMonsterDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumMonsterDeath();
        this.bantimeByPlaytimeMinimumEnvironmentDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumEnvironmentDeath();
        this.combatLogTime = this.plugin.getLDBConfig().getCombatTagTime();
        this.bantimeByPlaytimeGrowth = this.plugin.getLDBConfig().getBantimeByPlaytimeGrowth();
        this.bantimeByPlaytimePercent = this.plugin.getLDBConfig().getBantimeByPlaytimePercent();
        this.bantimeByPlaytime = this.plugin.getLDBConfig().isBantimeByPlaytime();
        this.combatLogWarningStyle = this.plugin.getLDBConfig().getCombatTagWarningStyle();
        this.bantimeByPlaytimeSinceLastDeath = this.plugin.getLDBConfig().isBantimeByPlaytimeSinceLastDeath();
        this.saveDateFormat = this.plugin.getLDBConfig().getSaveDateFormat();
        this.bantimeOnReviveDeath = this.plugin.getLDBConfig().getBantimeOnReviveDeath();
        this.combatTagPlayerKickDeath = this.plugin.getLDBConfig().isCombatTagPlayerKickDeath();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            e.setKickMessage(Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getPlayer().getName()).getReason());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!LiteDeathBanCRUD.doesPlayerDataExists(e.getPlayer().getUniqueId().toString(), this.plugin)) {
            new LiteDeathBanCRUD(e.getPlayer(), this.plugin).setNewStart();
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e instanceof EntityDamageByEntityEvent) {
                Player plyr = ((Player) e.getEntity());
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) e;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    this.tagPlayer(plyr, ((Player) lastEntityDamageEvent.getDamager()).getName());
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                this.tagPlayer(plyr, ((Player) a.getShooter()).getName());
                            }
                            break;
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                this.tagPlayer(plyr, ((Player) t.getShooter()).getName());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (!this.combatTagPlayerKickDeath) {
            this.kickList.add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player plyr = e.getPlayer();
        if (this.combatLog && this.plugin.doesTagListContain(plyr.getUniqueId()) && !e.getPlayer().hasPermission("litedeathban.bypass.combatlog") && !plyr.isBanned() && !this.kickList.contains(plyr.getUniqueId())) {
            plyr.setHealth(0.0D);
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
        if (this.plugin.getLDBConfig().isNotifyOnRespawn() && !e.getPlayer().hasPermission("litedeathban.bypass.ban")) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder(this.plugin.getMessages().getOnPlayerRespawn(e.getPlayer().getName(), crud.getLives())).create());
        }
    }

    private void tagPlayer(Player plyr, String taggedBy) {
        UUID plyrID = plyr.getUniqueId();
        if (plyr.getHealth() != 0D && !plyr.hasPermission("litedeathban.bypass.combatlog")) {
            boolean containsTag = this.plugin.doesTagListContain(plyrID);
            switch (this.combatLogWarningStyle.toLowerCase()) {
                case "none":
                    break;
                case "bossbar":
                    if (containsTag) {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask bossBarTask = new CombatLogBossBarWarning(this.plugin, this.combatLogTime, plyr, taggedBy).runTaskTimer(this.plugin, 0, 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), bossBarTask.getTaskId());
                    break;

                case "chat":
                    if (!containsTag) {
                        plyr.spigot().sendMessage(new ComponentBuilder(this.plugin.getMessages().getOnCombatTaggedChat(plyr.getName(), taggedBy, this.combatLogTime)).create());
                    } else {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask chatTask = new CombatLogChatWarning(this.plugin, plyr, taggedBy).runTaskLater(this.plugin, this.combatLogTime * 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), chatTask.getTaskId());
                    break;
            }
        }
    }

    private void deathBan(Player plyr) {
        LocalDateTime now = LocalDateTime.now();
        if (!plyr.hasPermission("litedeathban.bypass.ban")) {
            LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
            crud.setLives(crud.getLives() - 1, false);
            if (crud.getLives() == 0) {
                int bantime = this.getBanTime(plyr);
                switch (bantime) {
                    case -1:
                        break;
                    default:
                        crud.setTotalDeathBans(crud.getTotalDeathBans() + 1, false);
                        String banMessage = this.plugin.getMessages().getOnPlayerDeathBan(plyr.getName(), bantime, this.saveDateFormat.format(now.plusMinutes(bantime)), crud.getLastBanDate(), crud.getTotalDeathBans());
                        Bukkit.getBanList(BanList.Type.NAME).addBan(plyr.getName(), banMessage, Timestamp.valueOf(now.plusMinutes(bantime)), "LiteDeathBan");
                        Bukkit.getScheduler().runTask(this.plugin, () -> {
                            plyr.kickPlayer(banMessage);
                        });
                        crud.setLastBanDate(now, true);
                        break;
                }
            } else {
                crud.saveConfig();
            }
            if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
                Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                this.plugin.removeFromTagList(plyr.getUniqueId());
            }
        }
    }

    private int getBanTime(Player plyr) {
        if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
            int bantime;
            if (this.bantimeByPlaytime && this.bantimeByPlaytimeSinceLastDeath) {
                int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.TIME_SINCE_DEATH) / 20 / 60) / this.bantimeByPlaytimeInterval;
                bantime = this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumPlayerDeath, amountOfIntervalsPassed, this.playerDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
            } else if (this.bantimeByPlaytime) {
                int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60) / this.bantimeByPlaytimeInterval;
                bantime = this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumPlayerDeath, amountOfIntervalsPassed, this.playerDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
            } else {
                bantime = this.playerDeathBantime;
            }
            if (this.plugin.doesUsedReviveContain(plyr.getUniqueId())) {
                this.plugin.removeFromUsedRevive(plyr.getUniqueId());
                return bantime > this.bantimeOnReviveDeath ? bantime : this.bantimeOnReviveDeath;
            } else {
                return bantime;
            }
        }
        if (this.bantimeByPlaytime && this.bantimeByPlaytimeSinceLastDeath) {
            int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.TIME_SINCE_DEATH) / 20 / 60) / this.bantimeByPlaytimeInterval;
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumMonsterDeath, amountOfIntervalsPassed, this.monsterDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
                case "ENVIRONMENT":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumEnvironmentDeath, amountOfIntervalsPassed, this.environmentDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
                default:
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumPlayerDeath, amountOfIntervalsPassed, this.playerDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
            }
        } else if (this.bantimeByPlaytime) {
            int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60) / this.bantimeByPlaytimeInterval;
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumMonsterDeath, amountOfIntervalsPassed, this.monsterDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
                case "ENVIRONMENT":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumEnvironmentDeath, amountOfIntervalsPassed, this.environmentDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
                default:
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumPlayerDeath, amountOfIntervalsPassed, this.playerDeathBantime, this.bantimeByPlaytimeGrowth, this.bantimeByPlaytimePercent);
            }
        } else {
            switch (this.getDeathCause(plyr)) {
                case "MONSTER":
                    return this.monsterDeathBantime;
                case "ENVIRONMENT":
                    return this.environmentDeathBantime;
                default:
                    return this.playerDeathBantime;
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
                int bantimeLinear = min * (percent * intervalsPassed / 100 + 1);
                switch (max) {
                    case 0:
                        return bantimeLinear;
                    case -1:
                        return max;
                    default:
                        return bantimeLinear > max ? max : bantimeLinear;
                }
            case "exponential":
                int bantimeExponential = (int) (min * Math.pow(((double) percent / 100 + 1), intervalsPassed));
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
}
