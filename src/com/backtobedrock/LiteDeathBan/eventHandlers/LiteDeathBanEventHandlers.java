package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.helperClasses.CombatLogBossBarWarning;
import com.backtobedrock.LiteDeathBan.helperClasses.CombatLogChatWarning;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author PC_Nathan
 */
public class LiteDeathBanEventHandlers implements Listener {

    private final LiteDeathBan plugin;
    private static final Logger log = Bukkit.getLogger();

    private final Boolean combatLog;
    private final int playerDeathBantime;
    private final int monsterDeathBantime;
    private final int environmentDeathBantime;
    private final int bantimeByPlaytimeInterval;
    private final int bantimeByPlaytimeMinimumPlayerDeath;
    private final int bantimeByPlaytimeMinimumMonsterDeath;
    private final int bantimeByPlaytimeMinimumEnvironmentDeath;
    private final int combatLogTime;
    private final String bantimeByPlaytimeGrowth;
    private final int bantimeByPlaytimePercent;
    private final boolean bantimeByPlaytime;
    private final String combatLogWarningStyle;

    public LiteDeathBanEventHandlers(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.combatLog = this.plugin.getLDBConfig().isCombatLog();
        this.playerDeathBantime = this.plugin.getLDBConfig().getPlayerDeathBantime();
        this.monsterDeathBantime = this.plugin.getLDBConfig().getMonsterDeathBantime();
        this.environmentDeathBantime = this.plugin.getLDBConfig().getEnvironmentDeathBantime();
        this.bantimeByPlaytimeInterval = this.plugin.getLDBConfig().getBantimeByPlaytimeInterval();
        this.bantimeByPlaytimeMinimumPlayerDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumPlayerDeath();
        this.bantimeByPlaytimeMinimumMonsterDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumMonsterDeath();
        this.bantimeByPlaytimeMinimumEnvironmentDeath = this.plugin.getLDBConfig().getBantimeByPlaytimeMinimumEnvironmentDeath();
        this.combatLogTime = this.plugin.getLDBConfig().getCombatLogTime();
        this.bantimeByPlaytimeGrowth = this.plugin.getLDBConfig().getBantimeByPlaytimeGrowth();
        this.bantimeByPlaytimePercent = this.plugin.getLDBConfig().getBantimeByPlaytimePercent();
        this.bantimeByPlaytime = this.plugin.getLDBConfig().isBantimeByPlaytime();
        this.combatLogWarningStyle = this.plugin.getLDBConfig().getCombatLogWarningStyle();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!LiteDeathBanCRUD.doesPlayerDataExists(e.getPlayer().getUniqueId().toString())) {
            new LiteDeathBanCRUD(e.getPlayer()).setNewStart();
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e instanceof EntityDamageByEntityEvent) {
                Player plyr = ((Player) e.getEntity());
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) e;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    this.tagPlayer(plyr);
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                this.tagPlayer(plyr);
                            }
                            break;
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                this.tagPlayer(plyr);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void tagPlayer(Player plyr) {
        UUID plyrID = plyr.getUniqueId();
        if (plyr.getHealth() != 0D && !plyr.hasPermission("ldb.bypass.combatlog")) {
            boolean contains = this.plugin.doesTagListContain(plyrID);
            switch (this.combatLogWarningStyle.toLowerCase()) {
                case "none":
                    break;
                case "bossbar":
                    if (contains) {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                        this.plugin.removeFromTagList(plyr.getUniqueId());
                    }
                    BukkitTask bossBarTask = new CombatLogBossBarWarning(this.plugin, this.combatLogTime, plyr).runTaskTimer(this.plugin, 0, 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), bossBarTask.getTaskId());
                    break;

                case "chat":
                    if (!contains) {
                        plyr.spigot().sendMessage(new ComponentBuilder("You have been combat tagged!").create());
                    } else {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                    }
                    BukkitTask chatTask = new CombatLogChatWarning(this.plugin, plyr).runTaskLater(this.plugin, this.combatLogTime * 20);
                    this.plugin.addToTagList(plyr.getUniqueId(), chatTask.getTaskId());
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player plyr = e.getPlayer();
        if (!e.getPlayer().hasPermission("ldb.bypass.combatlog") && this.combatLog) {
            if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
                Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
                this.plugin.removeFromTagList(plyr.getUniqueId());
                plyr.setHealth(0.0D);
                if (!e.getPlayer().hasPermission("ldb.bypass.ban")) {
                    //ban
                    //quitmessage}
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player plyr = e.getEntity();
        if (this.plugin.doesTagListContain(plyr.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(plyr.getUniqueId()));
            this.plugin.removeFromTagList(plyr.getUniqueId());
        }
        if (!plyr.hasPermission("ldb.bypass.ban")) {
            LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr);
            crud.setLives(crud.getConfig().getInt("lives") - 1);
            if (crud.getConfig().getInt("lives") == 0) {
                int bantime = this.getBanTime(plyr);
                switch (bantime) {
                    case -1:
                        break;
                    default:
                        //ban
                        plyr.spigot().sendMessage(new ComponentBuilder("You have been banned for " + bantime).create());
                        crud.saveConfig();
                        break;
                }
            } else {
                crud.saveConfig();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(e.getPlayer());
        if (crud.getConfig().getInt("lives") == 0) {
            crud.setNewStart();
        }
        if (this.plugin.getLDBConfig().isNotifyLivesLeftOnRespawn() && !e.getPlayer().hasPermission("ldb.bypass.ban")) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder(String.format("You have %d %s left, use %s wisely!", crud.getConfig().getInt("lives"), crud.getConfig().getInt("lives") == 1 ? "life" : "lives", crud.getConfig().getInt("lives") == 1 ? "it" : "them")).create());
        }
    }

    private int getBanTime(Player plyr) {
        int amountOfIntervalsPassed = (plyr.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60) / this.bantimeByPlaytimeInterval;

        if (this.bantimeByPlaytime) {
            switch (this.getDeathCause(plyr)) {
                case "PLAYER":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumPlayerDeath, amountOfIntervalsPassed, this.playerDeathBantime);
                case "MONSTER":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumMonsterDeath, amountOfIntervalsPassed, this.monsterDeathBantime);
                case "ENVIRONMENT":
                    return this.linearVsExponentialBantime(this.bantimeByPlaytimeMinimumEnvironmentDeath, amountOfIntervalsPassed, this.environmentDeathBantime);
                default:
                    return -1;

            }
        } else {
            switch (this.getDeathCause(plyr)) {
                case "PLAYER":
                    return this.playerDeathBantime;
                case "MONSTER":
                    return this.monsterDeathBantime;
                case "ENVIRONMENT":
                    return this.environmentDeathBantime;
                default:
                    return -1;
            }
        }
    }

    private String getDeathCause(Player plyr) {
        EntityDamageEvent lastDamageCause = plyr.getLastDamageCause();
        if (lastDamageCause != null) {
            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) lastDamageCause;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    return "PLAYER";
                } else if (lastEntityDamageEvent.getDamager() instanceof Monster) {
                    return this.DeathCauseIfCombatDeath(plyr, "MONSTER");
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                return "PLAYER";
                            } else if (a.getShooter() instanceof Monster) {
                                return this.DeathCauseIfCombatDeath(plyr, "MONSTER");
                            } else {
                                return this.DeathCauseIfCombatDeath(plyr, "ENVIRONMENT");
                            }
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                return "PLAYER";
                            } else if (t.getShooter() instanceof Monster) {
                                return this.DeathCauseIfCombatDeath(plyr, "MONSTER");
                            } else {
                                return this.DeathCauseIfCombatDeath(plyr, "ENVIRONMENT");
                            }
                        default:
                            return this.DeathCauseIfCombatDeath(plyr, "MONSTER");
                    }
                }
            } else {
                return this.DeathCauseIfCombatDeath(plyr, "ENVIRONMENT");
            }
        } else {
            log.warning("[LiteDeathBan] Something went wrong, please contact the plugin author with the following code: 404");
            return "OTHER";
        }
    }

    private String DeathCauseIfCombatDeath(Player plyr, String lastDamager) {
        if (this.combatLog && this.plugin.doesTagListContain(plyr.getUniqueId())) {
            return "PLAYER";
        } else {
            return lastDamager;
        }
    }

    private int linearVsExponentialBantime(int min, int intervalsPassed, int max) {
        switch (this.bantimeByPlaytimeGrowth) {
            case "linear":
                int bantimeLinear = min * (this.bantimeByPlaytimePercent * intervalsPassed / 100 + 1);
                switch (max) {
                    case 0:
                        return bantimeLinear;
                    case -1:
                        return max;
                    default:
                        return bantimeLinear > max ? max : bantimeLinear;
                }
            case "exponential":
                int bantimeExponential = (int) (min * Math.pow(((double) this.bantimeByPlaytimePercent / 100 + 1), intervalsPassed));
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
