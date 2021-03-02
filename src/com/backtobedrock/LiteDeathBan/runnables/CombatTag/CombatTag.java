//package com.backtobedrock.LiteDeathBan.runnables.CombatTag;
//
//import com.backtobedrock.LiteDeathBan.LiteDeathBan;
//import com.backtobedrock.LiteDeathBan.domain.Killer;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitRunnable;
//
//public abstract class CombatTag implements BukkitRunnable {
//
//    protected final LiteDeathBan plugin;
//    protected final int time;
//    protected int timer;
//    protected final Killer tagger;
//    protected final Player player;
//
//    public CombatTag(Player player, Killer tagger) {
//        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
//        this.time = this.plugin.getConfiguration();
//        this.timer = this.time;
//        this.tagger = tagger;
//        this.player = player;
//    }
//
//    public Killer getTagger() {
//        return tagger;
//    }
//
//    public void start() {
//        this.runTaskTimer(this.plugin, 0, this.time);
//    }
//
//    public void stop() {
//        this.cancel();
//    }
//
//    protected abstract void timerTask();
//
//    @Override
//    public void run() {
//        if (this.timer > 0) {
//            this.timerTask();
//            this.timer--;
//        } else {
//            this.stop();
//        }
//    }
//
//    ;
//}
