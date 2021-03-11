package com.backtobedrock.LiteDeathBan.domain.enums;

public enum DamageCause {
    BLOCK_EXPLOSION(4320, "an explosion"), //works
    CONTACT(4320, "contact with a dangerous block"), //works
    CRAMMING(4320, "cramming"), //works
    DRAGON_BREATH(2880, "dragon breath"), //impossible
    DROWNING(4320, "drowning"), //works
    ENTITY_ATTACK(2880, "an entity attack"), //works
    PLAYER_ENTITY_ATTACK(7200, "a player attack"), //works
    ENTITY_EXPLOSION(2880, "an entity explosion"), //works
    PLAYER_ENTITY_EXPLOSION(7200, "a player explosion"), //can't test
    ENTITY_SWEEP_ATTACK(2880, "an entity sweep attack"), //can't test
    PLAYER_ENTITY_SWEEP_ATTACK(7200, "a player sweep attack"), //can't test
    FALL(4320, "falling"), //works
    FALLING_BLOCK(4320, "a falling block"), //works
    FIRE(4320, "fire"), //works
    FIRE_TICK(4320, "burning"), //works
    FLY_INTO_WALL(4320, "experiencing kinetic energy"), //works
    HOT_FLOOR(4320, "discovering the floor was hot"), //works, not as intended
    LAVA(4320, "lava"), //works
    LIGHTNING(4320, "being struck by lightning"), //works, not as intended
    MAGIC(2880, "magic"), //works
    PLAYER_MAGIC(7200, "player magic"), //impossible
    POISON(4320, "poison"), //impossible
    PROJECTILE(2880, "a projectile"), //works
    PLAYER_PROJECTILE(7200, "a player projectile"), //works
    STARVATION(4320, "starvation"), //works
    SUFFOCATION(4320, "suffocation"), //works
    SUICIDE(0, "suicide"), //works
    THORNS(2880, "trying to hurt something else (thorns)"), //works
    PLAYER_THORNS(7200, "player thorns"), //can't test
    VOID(4320, "falling out of the world"), //works
    WITHER(2880, "withering away"), //works
    COMBAT_LOG(2880, "combat logging"), //works
    PLAYER_COMBAT_LOG(7200, "combat logging"), //works
    REVIVE(7200, "reviving"); //

    private final int defaultBantime;
    private final String defaultDisplayName;

    DamageCause(int defaultBantime, String defaultDisplayName) {
        this.defaultBantime = defaultBantime;
        this.defaultDisplayName = defaultDisplayName;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    public int getDefaultBantime() {
        return defaultBantime;
    }
}
