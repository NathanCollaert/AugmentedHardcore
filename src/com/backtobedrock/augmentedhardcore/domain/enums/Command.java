package com.backtobedrock.augmentedhardcore.domain.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Command {
    AUGMENTEDHARDCORE(null, null, null, null, null),
    HELP(Permission.HELP, AUGMENTEDHARDCORE, Collections.emptyList(), Collections.emptyList(), "List of LiteDeathBan commands"),
    ADDLIVES(Permission.ADDLIVES, AUGMENTEDHARDCORE, Arrays.asList("player", "amount"), Collections.emptyList(), "Add lives to a player"),
    ADDLIFEPARTS(Permission.ADDLIFEPARTS, AUGMENTEDHARDCORE, Arrays.asList("player", "amount"), Collections.emptyList(), "Add life parts to a player"),
    SETLIVES(Permission.SETLIVES, AUGMENTEDHARDCORE, Arrays.asList("player", "amount"), Collections.emptyList(), "Set the lives of a player"),
    SETLIFEPARTS(Permission.SETLIFEPARTS, AUGMENTEDHARDCORE, Arrays.asList("player", "amount"), Collections.emptyList(), "Set the life parts of a player"),
    RELOAD(Permission.RELOAD, AUGMENTEDHARDCORE, Collections.emptyList(), Collections.emptyList(), "Reload LiteDeathBan"),
    UNDEATHBAN(Permission.UNDEATHBAN, null, Collections.singletonList("player"), Collections.emptyList(), "Unban a player that was death banned"),
    REVIVE(Permission.REVIVE, null, Collections.singletonList("player"), Collections.emptyList(), "Give a life of yours to someone else");

    private final Permission permission;
    private final Command parent;
    private final List<String> requiredParameters;
    private final List<String> optionalParameters;
    private final String description;

    Command(Permission permission, Command parent, List<String> requiredParameters, List<String> optionalParameters, String description) {
        this.permission = permission;
        this.parent = parent;
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
        this.description = description;
    }

    public Permission getPermission() {
        return permission;
    }

    public int getMinimumArguments() {
        return this.parent == null ? this.requiredParameters.size() : this.requiredParameters.size() + 1;
    }

    public int getMaximumArguments() {
        return this.parent == null ? (this.requiredParameters.size() + this.optionalParameters.size()) : (this.requiredParameters.size() + this.optionalParameters.size()) + 1;
    }

    public String getFancyVersion() {
        StringBuilder builder = new StringBuilder("§b/").append(this.parent == null ? this.name().toLowerCase() : this.parent.name().toLowerCase() + " " + this.name().toLowerCase());
        if (!this.requiredParameters.isEmpty()) {
            builder.append("§3");
            this.requiredParameters.forEach(e -> builder.append(" ").append("[").append(e).append("]"));
        }
        if (!this.optionalParameters.isEmpty()) {
            builder.append("§9");
            this.optionalParameters.forEach(e -> builder.append(" ").append("(").append(e).append(")"));
        }
        builder.append(" §8§l- §7").append(this.description).append(".");
        return builder.toString();
    }
}