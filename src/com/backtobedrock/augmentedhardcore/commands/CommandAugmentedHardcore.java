package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.utilities.CommandUtils;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandAugmentedHardcore extends AbstractCommand {
    public CommandAugmentedHardcore(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        if (this.args.length < 1) {
            if (this.hasPermission(Permission.HELP)) {
                this.sendHelpMessage();
            }
            return;
        }

        Command command = CommandUtils.getCommand(this.args[0]);
        if (command == null) {
            this.sendHelpMessage();
            return;
        }

        if (!this.hasPermission(command))
            return;

        if (!this.hasCorrectAmountOfArguments(command))
            return;

        switch (command) {
            case ADDLIVES:
                int amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.increaseLives(amount);

                        String lives = amount + (amount == 1 ? " life" : " lives"),
                                livesRaw = Integer.toString(amount),
                                livesTotal = playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"),
                                livesTotalRaw = Integer.toString(playerData.getLives());

                        this.sendSuccessMessages(
                                this.plugin.getMessages().getCommandAddLives(lives, livesRaw, livesTotal, livesTotalRaw),
                                this.plugin.getMessages().getCommandAddLivesSuccess(this.target.getName(), lives, livesRaw, livesTotal, livesTotalRaw)
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case ADDLIFEPARTS:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.increaseLifeParts(amount);

                        String lifeParts = amount + (amount == 1 ? " life part" : " life parts"),
                                lifePartsRaw = Integer.toString(amount),
                                lifePartsTotal = playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"),
                                lifePartsTotalRaw = Integer.toString(playerData.getLifeParts());

                        this.sendSuccessMessages(
                                this.plugin.getMessages().getCommandAddLifeParts(lifeParts, lifePartsRaw, lifePartsTotal, lifePartsTotalRaw),
                                this.plugin.getMessages().getCommandAddLifePartsSuccess(this.target.getName(), lifeParts, lifePartsRaw, lifePartsTotal, lifePartsTotalRaw)
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case SETLIVES:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.setLives(amount);

                        String lives = amount + (amount == 1 ? " life" : " lives"),
                                livesRaw = Integer.toString(amount),
                                livesTotal = playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"),
                                livesTotalRaw = Integer.toString(playerData.getLives());

                        this.sendSuccessMessages(
                                this.plugin.getMessages().getCommandSetLives(lives, livesRaw, livesTotal, livesTotalRaw),
                                this.plugin.getMessages().getCommandSetLivesSuccess(this.target.getName(), lives, livesRaw, livesTotal, livesTotalRaw)
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case SETLIFEPARTS:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.setLifeParts(amount);

                        String lifeParts = amount + (amount == 1 ? " life part" : " life parts"),
                                lifePartsRaw = Integer.toString(amount),
                                lifePartsTotal = playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"),
                                lifePartsTotalRaw = Integer.toString(playerData.getLifeParts());

                        this.sendSuccessMessages(
                                this.plugin.getMessages().getCommandSetLifeParts(lifeParts, lifePartsRaw, lifePartsTotal, lifePartsTotalRaw),
                                this.plugin.getMessages().getCommandSetLifePartsSuccess(this.target.getName(), lifeParts, lifePartsRaw, lifePartsTotal, lifePartsTotalRaw)
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case ADDMAXHEALTH:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    Player target = this.isTargetOnline();
                    if (target == null) {
                        return;
                    }

                    PlayerUtils.setMaxHealth(target, PlayerUtils.getMaxHealth(target) + amount);

                    String maxHealth = amount + " max health",
                            maxHealthRaw = Integer.toString(amount),
                            maxHealthTotal = (int) PlayerUtils.getMaxHealth(target) + " max health",
                            maxHealthTotalRaw = Integer.toString((int) PlayerUtils.getMaxHealth(target));

                    this.sendSuccessMessages(
                            this.plugin.getMessages().getCommandAddMaxHealth(maxHealth, maxHealthRaw, maxHealthTotal, maxHealthTotalRaw),
                            this.plugin.getMessages().getCommandAddMaxHealthSuccess(this.target.getName(), maxHealth, maxHealthRaw, maxHealthTotal, maxHealthTotalRaw)
                    );
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case SETMAXHEALTH:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    Player target = this.isTargetOnline();
                    if (target == null) {
                        return;
                    }

                    PlayerUtils.setMaxHealth(target, amount);

                    String maxHealth = amount + " max health",
                            maxHealthRaw = Integer.toString(amount);

                    this.sendSuccessMessages(
                            this.plugin.getMessages().getCommandSetMaxHealth(maxHealth, maxHealthRaw),
                            this.plugin.getMessages().getCommandSetMaxHealthSuccess(this.target.getName(), maxHealth, maxHealthRaw)
                    );
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
                break;
            case RESET:
                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.reset();

                        this.cs.sendMessage(this.plugin.getMessages().getCommandResetSuccess(this.target.getName()));
                    });
                });
                break;
            case RELOAD:
                this.plugin.initialize();
                this.cs.sendMessage(this.plugin.getMessages().getCommandReloadSuccess(this.plugin.getName()));
                break;
            default:
                this.sendHelpMessage();
        }
    }

    private void sendHelpMessage() {
        List<String> helpMessage = new ArrayList<>();
        helpMessage.add(this.plugin.getMessages().getCommandHelpHeader());
        Arrays.stream(Command.values()).filter(e -> {
            if (e.getPermission() == null) {
                return false;
            }

            return this.cs.hasPermission(e.getPermission().getPermissionString());
        }).forEach(e -> helpMessage.add(e.getFancyVersion()));
        helpMessage.add(this.plugin.getMessages().getCommandHelpFooter());
        this.cs.sendMessage(helpMessage.toArray(new String[0]));
    }

    private void sendSuccessMessages(String receiverSuccessMessage, String senderSuccessMessage) {
        if ((!(this.cs instanceof Player) || receiverSuccessMessage.isEmpty() || !((Player) this.cs).getUniqueId().toString().equals(this.target.getUniqueId().toString())) && !senderSuccessMessage.isEmpty()) {
            this.cs.sendMessage(senderSuccessMessage);
        }

        if (this.target.getPlayer() != null && !receiverSuccessMessage.isEmpty()) {
            this.target.getPlayer().sendMessage(receiverSuccessMessage);
        }
    }
}
