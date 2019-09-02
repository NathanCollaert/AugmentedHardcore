package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.LiteDeathBanPlayerData;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
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
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

/**
 *
 * @author PC_Nathan
 */
public class LiteDeathBanEventHandlers implements Listener {

    private final LiteDeathBan plugin;
    private static final Logger log = Bukkit.getLogger();
    private TreeMap<UUID, Date> damageLog = new TreeMap<>();

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
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (LiteDeathBanCRUD.getInstance().getPlayerDataWithID(e.getPlayer().getUniqueId()) == null) {
            LiteDeathBanCRUD.getInstance().addPlayerData(new LiteDeathBanPlayerData(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) e;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    if (this.damageLog.replace(((Player) e.getEntity()).getUniqueId(), new Date()) == null) {
                        this.damageLog.put(((Player) e.getEntity()).getUniqueId(), new Date());
                    }
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                if (this.damageLog.replace(((Player) e.getEntity()).getUniqueId(), new Date()) == null) {
                                    this.damageLog.put(((Player) e.getEntity()).getUniqueId(), new Date());
                                }
                            }

//                            BossBar bossBar = BossBarAPI.addBar(((Player) e.getEntity()), // The receiver of the BossBar
//                                    new TextComponent("You have been combat tagged, do not log out!"), // Displayed message
//                                    BossBarAPI.Color.RED, // Color of the bar
//                                    BossBarAPI.Style.PROGRESS, // Bar style
//                                    1.0f, // Progress (0.0 - 1.0)
//                                    this.combatLogTime, // Timeout
//                                    2); // Timeout-interval
                            break;
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                if (this.damageLog.replace(((Player) e.getEntity()).getUniqueId(), new Date()) == null) {
                                    this.damageLog.put(((Player) e.getEntity()).getUniqueId(), new Date());
                                }
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
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (true && this.combatLog) {//has permission
            Player plyr = e.getPlayer();
            System.out.println(this.damageLog.containsKey(plyr.getUniqueId()));
            if (this.damageLog.containsKey(plyr.getUniqueId())) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(this.damageLog.get(plyr.getUniqueId()));
                cal.add(Calendar.SECOND, this.combatLogTime);
                if (cal.getTime().after(new Date())) {
                    plyr.setHealth(0.0D);
                    //ban
                    //quitmessage
                    this.damageLog.remove(plyr.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player plyr = e.getEntity();
        if (true) {//if permission
            LiteDeathBanPlayerData plyrData = LiteDeathBanCRUD.getInstance().getPlayerDataWithID(plyr.getUniqueId());
            plyrData.setLives(plyrData.getLives() - 1);
            if (plyrData.getLives() == 0) {
                int bantime = this.getBanTime(plyr);
                switch (bantime) {
                    case -1:
                        plyrData.setLives(plyrData.getLives() + 1);
                        break;
                    default:
                        //ban
                        //kick + message
                        LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
                        break;
                }
            } else {
                LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
            }
        }
        this.damageLog.remove(plyr.getUniqueId());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        LiteDeathBanPlayerData plyrData = LiteDeathBanCRUD.getInstance().getPlayerDataWithID(e.getPlayer().getUniqueId());
        if (plyrData != null && plyrData.getLives() == 0) {
            plyrData.setLives(1);
            LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
        }
        if (this.plugin.getLDBConfig().isNotifyLivesLeftOnRespawn()) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder(String.format("You have %d %s left, use %s wisely!", plyrData.getLives(), plyrData.getLives() == 1 ? "life" : "lives", plyrData.getLives() == 1 ? "it" : "them")).create());
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
            log.warning("Something went wrong, please contact the plugin author with the following code: 404");
            return "OTHER";
        }
    }

    private String DeathCauseIfCombatDeath(Player plyr, String lastDamager) {
        Date lastDate = new Date();
        Date secondToLastDate = this.damageLog.get(plyr.getUniqueId());
        if (this.combatLog && secondToLastDate != null) {//check for kick && crash
            Calendar cal = Calendar.getInstance();
            cal.setTime(secondToLastDate);
            cal.add(Calendar.SECOND, this.combatLogTime);
            if (cal.getTime().after(lastDate)) {
                return "PLAYER";
            } else {
                return lastDamager;
            }
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
