package com.backtobedrock.augmentedhardcore.mappers.player.patches;

import com.backtobedrock.augmentedhardcore.mappers.Patch;

public class LastDeathAdditionPatch extends Patch {
    @Override
    protected boolean hasBeenApplied() {
        return this.containsLastDeath();
    }

    private boolean containsLastDeath() {
        return this.doesColumnExist("ah_player", "last_death");
    }

    @Override
    protected void applyPatch() {
        String sql = "ALTER TABLE ah_player "
                + "ADD COLUMN `last_death` DATETIME NULL "
                + "AFTER `last_known_ip`;";
        this.execute(sql);
    }
}
