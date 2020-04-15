package io.github.lokka30.phantomboard.commands;

import io.github.lokka30.phantomboard.PhantomBoard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhantomBoardCommand implements TabExecutor {

    private PhantomBoard instance;

    public PhantomBoardCommand(final PhantomBoard instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label, @NotNull final String[] args) {
        if (args.length == 1 || args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "toggle":
                    if (sender.hasPermission("phantomboard.toggle")) {
                        if (args.length == 1) {
                            if (sender instanceof Player) {
                                final Player player = (Player) sender;
                                instance.getScoreboardManager().toggleHidden(player.getUniqueId());
                                player.sendMessage(instance.getUtils().colorize(instance.getMessages().get("toggle-self", "Toggled your visibility of the scoreboard.")));
                            } else {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("toggle-usage-console", "Usage (console): /board toggle <player>")));
                            }
                        } else if (args.length == 2) {
                            final Player target = Bukkit.getPlayer(args[1]);

                            if (target == null) {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("player-not-online", "%player% isn't online."))
                                        .replaceAll("%player%", args[1]));
                            } else {
                                instance.getScoreboardManager().toggleHidden(target.getUniqueId());
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("toggle-others", "Toggled %player%'s visibility of the scoreboard."))
                                        .replaceAll("%player%", target.getName()));
                            }
                        } else {
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("toggle-usage", "Usage: /board toggle [player]")));
                        }
                    } else {
                        sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("no-permission", "You don't have access to that.")));
                    }
                    break;
                case "on":
                    if (sender.hasPermission("phantomboard.toggle")) {
                        if (args.length == 1) {
                            if (sender instanceof Player) {
                                final Player player = (Player) sender;
                                final UUID uuid = player.getUniqueId();

                                if (instance.getScoreboardManager().isHidden(uuid)) {
                                    instance.getScoreboardManager().setHidden(uuid, false);
                                    player.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-self", "The board is now visible.")));
                                } else {
                                    player.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-self-already", "The board is already visible.")));
                                }
                            } else {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-usage-console", "Usage (console): /board on <player>")));
                            }
                        } else if (args.length == 2) {
                            final Player target = Bukkit.getPlayer(args[1]);

                            if (target == null) {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("player-not-online", "%player% isn't online."))
                                        .replaceAll("%player%", args[1]));
                            } else {
                                final UUID uuid = target.getUniqueId();

                                if (instance.getScoreboardManager().isHidden(uuid)) {
                                    instance.getScoreboardManager().setHidden(uuid, false);
                                    sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-others", "The board is now visible to %player%."))
                                            .replaceAll("%player%", target.getName()));
                                    target.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-by", "%sender% made your board visible."))
                                            .replaceAll("%sender%", sender.getName()));
                                } else {
                                    sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-others-already", "%player%'s board is already visible."))
                                            .replaceAll("%player%", target.getName()));
                                }
                            }
                        } else {
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-on-usage", "Usage: /board on [player]")));
                        }
                    } else {
                        sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("no-permission", "You don't have access to that.")));
                    }
                    break;
                case "off":
                    if (sender.hasPermission("phantomboard.toggle")) {
                        if (args.length == 1) {
                            if (sender instanceof Player) {
                                final Player player = (Player) sender;
                                final UUID uuid = player.getUniqueId();

                                if (instance.getScoreboardManager().isHidden(uuid)) {
                                    player.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-self-already", "The board is already hidden.")));
                                } else {
                                    instance.getScoreboardManager().setHidden(uuid, true);
                                    player.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-self", "The board is now hidden.")));
                                }
                            } else {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-usage-console", "Usage (console): /board off <player>")));
                            }
                        } else if (args.length == 2) {
                            final Player target = Bukkit.getPlayer(args[1]);

                            if (target == null) {
                                sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("player-not-online", "%player% isn't online."))
                                        .replaceAll("%player%", args[1]));
                            } else {
                                final UUID uuid = target.getUniqueId();

                                if (instance.getScoreboardManager().isHidden(uuid)) {
                                    sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-others-already", "%player%'s board is already hidden."))
                                            .replaceAll("%player%", target.getName()));
                                } else {
                                    instance.getScoreboardManager().setHidden(uuid, true);
                                    sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-others", "The board is now hidden to %player%."))
                                            .replaceAll("%player%", target.getName()));
                                    target.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-by", "%sender% made your board hidden."))
                                            .replaceAll("%sender%", sender.getName()));
                                }
                            }
                        } else {
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("set-off-usage", "Usage: /board off [player]")));
                        }
                    } else {
                        sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("no-permission", "You don't have access to that.")));
                    }
                    break;
                case "info":
                    if (args.length == 1) {

                        String compatibleServerVersionString, runningPAPIString, settingsVersionString, messagesVersionString, dataVersionString;

                        if (instance.getServer().getVersion().contains(instance.getUtils().getRecommendedServerVersion())) {
                            compatibleServerVersionString = "&aYes (recommended)";
                        } else {
                            compatibleServerVersionString = "&cNo";
                        }

                        if (instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
                            runningPAPIString = "&cNo";
                        } else {
                            runningPAPIString = "&aYes (recommended)";
                        }

                        if (instance.getSettings().get("file-version", 0) == instance.getUtils().getRecommendedSettingsVersion()) {
                            settingsVersionString = "&aLatest (recommended)";
                        } else {
                            settingsVersionString = "&cOutdated";
                        }

                        if (instance.getMessages().get("file-version", 0) == instance.getUtils().getRecommendedMessagesVersion()) {
                            messagesVersionString = "&aLatest (recommended)";
                        } else {
                            messagesVersionString = "&cOutdated";
                        }

                        if (instance.getData().get("file-version", 0) == instance.getUtils().getRecommendedDataVersion()) {
                            dataVersionString = "&aLatest (recommended)";
                        } else {
                            dataVersionString = "&cOutdated";
                        }

                        sender.sendMessage(" ");
                        sender.sendMessage("&8&m>>&a&l General Information");
                        sender.sendMessage(instance.getUtils().colorize("&7This server is running &a&lPhantomBoard &av" + instance.getDescription().getVersion() + "&7."));
                        sender.sendMessage(instance.getUtils().colorize("&7Contributors:"));
                        for (String contributor : instance.getDescription().getAuthors()) {
                            sender.sendMessage(instance.getUtils().colorize("&8 - &7" + contributor));
                        }
                        sender.sendMessage(" ");
                        sender.sendMessage("&8&m>>&a&l Compatibility Check");
                        sender.sendMessage(instance.getUtils().colorize("&7Running supported server version: " + compatibleServerVersionString));
                        sender.sendMessage(instance.getUtils().colorize("&7Running PAPI: " + runningPAPIString));
                        sender.sendMessage(instance.getUtils().colorize("&7settings.yml version: " + settingsVersionString));
                        sender.sendMessage(instance.getUtils().colorize("&7messages.yml version: " + messagesVersionString));
                        sender.sendMessage(instance.getUtils().colorize("&7data.json version: " + dataVersionString));
                        sender.sendMessage(" ");
                    } else {
                        sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("info-usage", "Usage: /board info")));
                    }
                    break;
                case "reload":
                    if (sender.hasPermission("phantomboard.reload")) {
                        if (args.length == 1) {
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("reload-started", "Reload started...")));
                            instance.getScoreboardManager().load();
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("reload-done", "...Reload done.")));
                        } else {
                            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("reload-usage", "Usage: /board reload")));
                        }
                    } else {
                        sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("no-permission", "You don't have access to that.")));
                    }
                    break;
            }
        } else {
            sender.sendMessage(instance.getUtils().colorize(instance.getMessages().get("usage", "Usage: /board <toggle/on/off/info/reload>")));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label, final String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            if (sender.hasPermission("phantomboard.toggle")) {
                suggestions.add("toggle");
                suggestions.add("on");
                suggestions.add("off");
            }

            suggestions.add("info");

            if (sender.hasPermission("phantomboard.reload")) {
                suggestions.add("reload");
            }
        } else if (args.length == 1) {
            if (sender.hasPermission("phantomboard.toggle")) {
                if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        suggestions.add(onlinePlayer.getName());
                    }
                }
            }
        }

        return suggestions;
    }
}
