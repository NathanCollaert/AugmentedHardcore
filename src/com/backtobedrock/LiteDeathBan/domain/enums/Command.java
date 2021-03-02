package com.backtobedrock.LiteDeathBan.domain.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Command {
    //TODO: make use of permission enum
    //PLACEHOLDER("permission.permission", "command", Collections.singletonList("requiredParameter"), Collections.singletonList("optionalParameter"), "Description"),
    HELP("help", "litedeathban help", Collections.emptyList(), Collections.emptyList(), "List of LiteDeathBan commands"),
    ADDLIVES("addlives", "litedeathban addlives", Arrays.asList("player", "amount"), Collections.emptyList(), "Add lives to a player"),
    ADDLIFEPARTS("addlifeparts", "litedeathban addlifeparts", Arrays.asList("player", "amount"), Collections.emptyList(), "Add life parts to a player"),
    SETLIVES("setlives", "litedeathban setlives", Arrays.asList("player", "amount"), Collections.emptyList(), "Set the lives of a player"),
    SETLIFEPARTS("setlifeparts", "litedeathban setlifeparts", Arrays.asList("player", "amount"), Collections.emptyList(), "Set the life parts of a player"),
    RELOAD("reload", "litedeathban reload", Collections.emptyList(), Collections.emptyList(), "Reload LiteDeathBan"),
    UNBAN("unban", "litedeathban unban", Collections.singletonList("player"), Collections.emptyList(), "Unban a player that was death banned.");

    private final String permission;
    private final String command;
    private final List<String> requiredParameters;
    private final List<String> optionalParameters;
    private final String description;

    Command(String permission, String command, List<String> requiredParameters, List<String> optionalParameters, String description) {
        this.permission = permission;
        this.command = command;
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public int getMinimumArguments() {
        return this.requiredParameters.size();
    }

    public int getMaximumArguments() {
        return (this.requiredParameters.size() + this.optionalParameters.size());
    }

    public String getFancyVersion() {
        StringBuilder builder = new StringBuilder("§b/").append(this.command);
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
