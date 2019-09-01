package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.LiteDeathBanPlayerData;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author PC_Nathan
 */
public class LiteDeathBanEventHandlers implements Listener {

    private final LiteDeathBan plugin;
    private final Logger log = Bukkit.getLogger();

    public LiteDeathBanEventHandlers(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (LiteDeathBanCRUD.getInstance().getPlayerDataWithID(e.getPlayer().getUniqueId()) == null) {
            LiteDeathBanCRUD.getInstance().addPlayerData(new LiteDeathBanPlayerData(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player plyr = e.getEntity();
        if (true) {
            LiteDeathBanPlayerData plyrData = LiteDeathBanCRUD.getInstance().getPlayerDataWithID(plyr.getUniqueId());
            plyrData.setLives(plyrData.getLives() - 1);
            if (plyrData.getLives() == 0) {
                int bantime = this.getBanTime(plyr);
                switch (bantime) {
                    case -1:
                        plyrData.setLives(plyrData.getLives() + 1);
                        break;
                    default:
                        log.info("Ban player for " + bantime);
                        LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
                        break;
                }
            } else {
                LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        LiteDeathBanPlayerData plyrData = LiteDeathBanCRUD.getInstance().getPlayerDataWithID(e.getPlayer().getUniqueId());
        if (plyrData != null && plyrData.getLives() == 0) {
            plyrData.setLives(1);
            LiteDeathBanCRUD.getInstance().updatePlayerData(plyrData);
        }
        if (this.plugin.getLDBConfig().isAnnounceLivesOnRespawn()) {
            e.getPlayer().spigot().sendMessage(new ComponentBuilder(String.format("You have %d %s left, use %s wisely!", plyrData.getLives(), plyrData.getLives() == 1 ? "life" : "lives", plyrData.getLives() == 1 ? "it" : "them")).create());
        }
    }

    private int getBanTime(Player plyr) {
        EntityDamageEvent lastDamageCause = plyr.getLastDamageCause();
        if (lastDamageCause != null) {
            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastEntityDamageEvent = (EntityDamageByEntityEvent) lastDamageCause;
                if (lastEntityDamageEvent.getDamager() instanceof Player) {
                    return this.plugin.getLDBConfig().getPlayerKillBantime();
                } else if (lastEntityDamageEvent.getDamager() instanceof Monster) {
                    return this.plugin.getLDBConfig().getMonsterKillBantime();
                } else {
                    switch (lastEntityDamageEvent.getDamager().getType()) {
                        case ARROW:
                            Arrow a = (Arrow) lastEntityDamageEvent.getDamager();
                            if (a.getShooter() instanceof Player) {
                                return this.plugin.getLDBConfig().getPlayerKillBantime();
                            } else if (a.getShooter() instanceof Monster) {
                                return this.plugin.getLDBConfig().getMonsterKillBantime();
                            } else {
                                return this.plugin.getLDBConfig().getEnvironmentKillBantime();
                            }
                        case TRIDENT:
                            Trident t = (Trident) lastEntityDamageEvent.getDamager();
                            if (t.getShooter() instanceof Player) {
                                return this.plugin.getLDBConfig().getPlayerKillBantime();
                            } else if (t.getShooter() instanceof Monster) {
                                return this.plugin.getLDBConfig().getMonsterKillBantime();
                            } else {
                                return this.plugin.getLDBConfig().getEnvironmentKillBantime();
                            }
                        default:
                            return this.plugin.getLDBConfig().getMonsterKillBantime();
                    }
                }
            } else {
                return this.plugin.getLDBConfig().getEnvironmentKillBantime();
            }
        } else {
            log.warning("Something went wrong, please contact the plugin author with the following code: 401");
            return -1;
        }
    }
}
