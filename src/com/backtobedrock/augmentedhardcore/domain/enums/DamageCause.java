package com.backtobedrock.augmentedhardcore.domain.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DamageCause {
    BLOCK_EXPLOSION(4320,
            Arrays.asList(
                    "blew up",
                    "got caught in an explosion"
            )
    ), //works
    CONTACT(4320,
            Arrays.asList(
                    "were pricked to death",
                    "were poked to death"
            )
    ), //works
    CRAMMING(4320,
            Arrays.asList(
                    "were squished too much",
                    "were flattened"
            )
    ), //works
    DRAGON_BREATH(2880,
            Collections.singletonList(
                    "were roasted in dragon breath"
            )
    ), //unable to replicate in vanilla
    DROWNING(4320,
            Arrays.asList(
                    "drowned",
                    "weren't able to find air"
            )
    ), //works
    ENTITY_ATTACK(2880,
            Arrays.asList(
                    "were slain",
                    "were doomed"
            )
    ), //works
    PLAYER_ENTITY_ATTACK(7200,
            Arrays.asList(
                    "were slain",
                    "were doomed"
            )
    ), //can't test solo
    ENTITY_EXPLOSION(2880,
            Arrays.asList(
                    "blew up",
                    "got caught in an explosion",
                    "went off with a bang"
            )
    ), //works
    PLAYER_ENTITY_EXPLOSION(7200,
            Arrays.asList(
                    "blew up",
                    "got caught in an explosion",
                    "went off with a bang"
            )
    ), //can't test solo
    ENTITY_SWEEP_ATTACK(2880,
            Arrays.asList(
                    "were slain during a sweep attack",
                    "were caught in a sweep attack"
            )
    ), //Can't test
    PLAYER_ENTITY_SWEEP_ATTACK(7200,
            Arrays.asList(
                    "were slain during a sweep attack",
                    "were caught in a sweep attack"
            )
    ), //can't test solo
    FALL(4320,
            Arrays.asList(
                    "hit the ground too hard",
                    "fell from a high place",
                    "fell too far"
            )
    ), //works
    FALLING_BLOCK(4320,
            Arrays.asList(
                    "were squashed",
                    "were crushed",
                    "were shattered"
            )
    ), //works
    FIRE(4320,
            Arrays.asList(
                    "went up in flames",
                    "walked into fire"
            )
    ), //works
    FIRE_TICK(4320,
            Arrays.asList(
                    "burned to death",
                    "burned to a crisp"
            )
    ), //works
    FLY_INTO_WALL(4320,
            Collections.singletonList(
                    "experienced kinetic energy"
            )
    ), //works
    HOT_FLOOR(4320,
            Arrays.asList(
                    "discovered the floor was %killer% and hot",
                    "walked into %killer% and melted"
            )
    ), //works
    LAVA(4320,
            Arrays.asList(
                    "tried to swim in lava",
                    "lost the floor is lava game"
            )
    ), //works
    LIGHTNING(4320,
            Collections.singletonList(
                    "were struck by"
            )
    ), //works
    MAGIC(2880,
            Collections.singletonList(
                    "were killed with magic"
            )
    ), //works
    PLAYER_MAGIC(7200,
            Collections.singletonList(
                    "were killed with magic"
            )
    ), //works
    POISON(4320,
            Arrays.asList(
                    "drank the wrong potion",
                    "got injected by poison"
            )
    ), //unable to replicate in vanilla
    PROJECTILE(2880,
            Arrays.asList(
                    "were shot",
                    "were impaled"
            )
    ),
    PLAYER_PROJECTILE(7200,
            Arrays.asList(
                    "were shot",
                    "were impaled"
            )
    ), //works
    STARVATION(4320,
            Arrays.asList(
                    "starved to death",
                    "couldn't find food"
            )
    ), //works
    SUFFOCATION(4320,
            Arrays.asList(
                    "suffocated",
                    "were too soft for this world"
            )
    ), //works
    SUICIDE(0,
            Arrays.asList(
                    "died",
                    "committed suicide"
            )
    ), //works
    THORNS(2880,
            Collections.singletonList(
                    "were killed due to deflected damage"
            )
    ),
    PLAYER_THORNS(7200,
            Collections.singletonList(
                    "were killed due to deflected damage"
            )
    ), //works
    VOID(4320,
            Arrays.asList(
                    "fell out of the world",
                    "didn't want to live in this world anymore",
                    "were sucked into the void"
            )
    ), //works
    WITHER(2880,
            Collections.singletonList(
                    "withered away"
            )
    ), //works
    COMBAT_LOG(2880,
            Collections.singletonList(
                    "combat logged"
            )
    ), //works
    PLAYER_COMBAT_LOG(7200,
            Collections.singletonList(
                    "combat logged"
            )
    ), //works
    REVIVE(7200,
            Collections.singletonList(
                    "revived"
            )
    ); //can't test solo

    private final int defaultBantime;
    private final List<String> defaultDisplayMessages;

    DamageCause(int defaultBantime, List<String> defaultDisplayMessages) {
        this.defaultBantime = defaultBantime;
        this.defaultDisplayMessages = defaultDisplayMessages;
    }

    public List<String> getDefaultDisplayMessages() {
        return defaultDisplayMessages;
    }

    public int getDefaultBantime() {
        return defaultBantime;
    }
}
