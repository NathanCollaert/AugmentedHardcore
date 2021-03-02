//package com.backtobedrock.LiteDeathBan.runnables.CombatTag;
//
//import com.backtobedrock.LiteDeathBan.domain.Killer;
//import org.bukkit.Bukkit;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
//import org.bukkit.entity.Player;
//
//public class BossBarTag extends CombatTag {
//    private final BossBar bar;
//
//    public BossBarTag(Player player, Killer tagger) {
//        super(player, tagger);
//        //TODO: get from config
//        this.bar = Bukkit.createBossBar(String.format("You've been tagged by %s", tagger.getTagMessage()), BarColor.RED, BarStyle.SOLID);
//    }
//
//    @Override
//    protected void timerTask() {
//        this.bar.setProgress((double) 1 / this.time * this.timer);
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        bar.addPlayer(this.player);
//        bar.setProgress(1);
//        bar.setVisible(true);
//    }
//}
