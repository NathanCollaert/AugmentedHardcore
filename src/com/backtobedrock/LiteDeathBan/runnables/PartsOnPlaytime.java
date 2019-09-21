package com.backtobedrock.LiteDeathBan.runnables;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.LiteDeathBanCRUD;
import com.backtobedrock.LiteDeathBan.eventHandlers.LiteDeathBanEventHandlers;
import org.bukkit.scheduler.BukkitRunnable;

public class PartsOnPlaytime extends BukkitRunnable {

    private final LiteDeathBan plugin;

    public PartsOnPlaytime(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getServer().getOnlinePlayers().stream().forEach(p -> {
            int playtimeParts = LiteDeathBanEventHandlers.checkPlaytimeForParts(p, this.plugin.getFromPlaytimeLastLifeOnlinePlayers(p.getUniqueId()), this.plugin.getLDBConfig().getPlaytimePerPart());
            if (playtimeParts > 0) {
                LiteDeathBanCRUD crud = new LiteDeathBanCRUD(p, this.plugin);
                crud.setLifeParts(crud.getLifeParts() + playtimeParts, false);
                crud.setLastPartPlaytime(crud.getLastPartPlaytime() + (playtimeParts * this.plugin.getLDBConfig().getPlaytimePerPart() * 60 * 20), true);
                this.plugin.addToPlaytimeLastLifeOnlinePlayers(p.getUniqueId(), crud.getLastPartPlaytime());
            }

        });
    }

}
