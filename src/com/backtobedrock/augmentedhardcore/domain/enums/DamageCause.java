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
    ),
    CONTACT(4320,
            Arrays.asList(
                    "were pricked to death",
                    "were poked to death"
            )
    ),
    CRAMMING(4320,
            Arrays.asList(
                    "were squished too much",
                    "were flattened"
            )
    ),
    DRAGON_BREATH(2880,
            Collections.singletonList(
                    "were roasted in dragon breath"
            )
    ),
    DROWNING(4320,
            Arrays.asList(
                    "drowned",
                    "weren't able to find air"
            )
    ),
    ENTITY_ATTACK(2880,
            Arrays.asList(
                    "were slain",
                    "were doomed"
            )
    ),
    PLAYER_ENTITY_ATTACK(7200,
            Arrays.asList(
                    "were slain",
                    "were doomed"
            )
    ),
    ENTITY_EXPLOSION(2880,
            Arrays.asList(
                    "blew up",
                    "got caught in an explosion",
                    "went off with a bang"
            )
    ),
    PLAYER_ENTITY_EXPLOSION(7200,
            Arrays.asList(
                    "blew up",
                    "got caught in an explosion",
                    "went off with a bang"
            )
    ),
    ENTITY_SWEEP_ATTACK(2880,
            Arrays.asList(
                    "were slain during a sweep attack",
                    "were caught in a sweep attack"
            )
    ),
    PLAYER_ENTITY_SWEEP_ATTACK(7200,
            Arrays.asList(
                    "were slain during a sweep attack",
                    "were caught in a sweep attack"
            )
    ),
    FALL(4320,
            Arrays.asList(
                    "hit the ground too hard",
                    "fell from a high place",
                    "fell too far"
            )
    ),
    FALLING_BLOCK(4320,
            Arrays.asList(
                    "were squashed",
                    "were crushed",
                    "were shattered"
            )
    ),
    FIRE(4320,
            Arrays.asList(
                    "went up in flames",
                    "walked into fire"
            )
    ),
    FIRE_TICK(4320,
            Arrays.asList(
                    "burned to death",
                    "burned to a crisp"
            )
    ),
    FLY_INTO_WALL(4320,
            Collections.singletonList(
                    "experienced kinetic energy"
            )
    ),
    FREEZE(4320,
            Collections.singletonList("froze to death")
    ),
    HOT_FLOOR(4320,
            Arrays.asList(
                    "discovered the floor was %killer% and hot",
                    "walked into %killer% and melted"
            )
    ),
    KILL(0,
            Arrays.asList(
                    "died",
                    "was killed"
            )),
    LAVA(4320,
            Arrays.asList(
                    "tried to swim in lava",
                    "lost the floor is lava game"
            )
    ),
    LIGHTNING(4320,
            Collections.singletonList(
                    "were struck by"
            )
    ),
    MAGIC(2880,
            Collections.singletonList(
                    "were killed with magic"
            )
    ),
    PLAYER_MAGIC(7200,
            Collections.singletonList(
                    "were killed with magic"
            )
    ),
    POISON(4320,
            Arrays.asList(
                    "drank the wrong potion",
                    "got injected by poison"
            )
    ),
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
    ),
    SONIC_BOOM(2880,
            Collections.singletonList(
                    "was obliterated by a sonically-charged shriek"
            )
    ),
    STARVATION(4320,
            Arrays.asList(
                    "starved to death",
                    "couldn't find food"
            )
    ),
    SUFFOCATION(4320,
            Arrays.asList(
                    "suffocated",
                    "were too soft for this world"
            )
    ),
    SUICIDE(0,
            Arrays.asList(
                    "died",
                    "committed suicide"
            )
    ),
    THORNS(2880,
            Collections.singletonList(
                    "were killed due to deflected damage"
            )
    ),
    PLAYER_THORNS(7200,
            Collections.singletonList(
                    "were killed due to deflected damage"
            )
    ),
    VOID(4320,
            Arrays.asList(
                    "fell out of the world",
                    "didn't want to live in this world anymore",
                    "were sucked into the void"
            )
    ),
    WITHER(2880,
            Collections.singletonList(
                    "withered away"
            )
    ),
    WORLD_BORDER(0,
            Collections.singletonList(
                    "left the confines of this world"
            )
    ),
    COMBAT_LOG(2880,
            Collections.singletonList(
                    "combat logged"
            )
    ),
    PLAYER_COMBAT_LOG(7200,
            Collections.singletonList(
                    "combat logged"
            )
    ),
    REVIVE(7200,
            Collections.singletonList(
                    "revived"
            )
    );

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

    @Override
    public String toString() {
        return this.name().toLowerCase().replaceAll("_", " ");
    }
}
