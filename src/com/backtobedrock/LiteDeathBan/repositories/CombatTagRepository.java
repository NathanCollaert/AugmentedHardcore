//package com.backtobedrock.LiteDeathBan.repositories;
//
//import com.backtobedrock.LiteDeathBan.LiteDeathBan;
//import com.backtobedrock.LiteDeathBan.runnables.CombatTag.CombatTag;
//import org.bukkit.OfflinePlayer;
//import org.bukkit.plugin.java.JavaPlugin;
//
//import java.util.HashMap;
//import java.util.UUID;
//
//public class CombatTagRepository {
//    private final LiteDeathBan plugin;
//    private final HashMap<UUID, CombatTag> combatTagged;
//
//    public CombatTagRepository() {
//        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
//        this.combatTagged = new HashMap<>();
//    }
//
//    public boolean isTagged(OfflinePlayer player) {
//        return this.combatTagged.containsKey(player.getUniqueId());
//    }
//
//    public void unTag(OfflinePlayer player) {
//        if (!this.isTagged(player)) {
//            return;
//        }
//        this.combatTagged.remove(player.getUniqueId()).start();
//    }
//
//    public void tag(OfflinePlayer player) {
//        CombatTag tag;
////        switch (this.plugin.getConfiguration().getCombatTagConfiguration().tagType){
////            case
////        }
////        this.combatTagged.put(player.getUniqueId(), tag);
////        tag.stop();
//    }
//
//    public CombatTag getTag(OfflinePlayer player) {
//        return this.combatTagged.get(player.getUniqueId());
//    }
//}
