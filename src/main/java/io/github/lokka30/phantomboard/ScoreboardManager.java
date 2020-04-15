package io.github.lokka30.phantomboard;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ScoreboardManager {

    private PhantomBoard instance;

    public ScoreboardManager(final PhantomBoard instance) {
        this.instance = instance;
    }

    //These players won't be shown the scoreboard.
    private ArrayList<UUID> hiddenPlayers;

    private ArrayList<Player> onlinePlayers;

    private String title;
    private List<String> lines;
    private int period;
    private boolean isRunning = false;
    private boolean isRefreshing = false;

    public void load() {
        onlinePlayers = new ArrayList<>();
        updateOnlinePlayers();

        hiddenPlayers = new ArrayList<>();
        updateHiddenPlayers();

        title = instance.getSettings().get("scoreboard.title", instance.getUtils().colorize("&a&nPhantomBoard"));
        lines = instance.getSettings().get("scoreboard.lines", Collections.singletonList(instance.getUtils().colorize("&7PhantomBoard by lokka30")));
        period = instance.getSettings().get("scoreboard.updatePeriod", 5);

        if (!isRunning) {
            scheduleTask();
        }
    }

    public void updateOnlinePlayers() {
        isRefreshing = true;
        onlinePlayers.clear();
        onlinePlayers.addAll(Bukkit.getOnlinePlayers());
        isRefreshing = false;
    }

    public void updateHiddenPlayers() {
        isRefreshing = true;
        for (Player player : onlinePlayers) {
            final UUID uuid = player.getUniqueId();

            if (instance.getData().get("players." + uuid.toString() + ".hidden", false)) {
                hiddenPlayers.add(uuid);
            }
        }
        isRefreshing = false;
    }

    public void updateOnlineAndHiddenPlayers() {
        updateOnlinePlayers();
        updateHiddenPlayers();
    }

    public void toggleHidden(final UUID uuid) {
        if (hiddenPlayers.contains(uuid)) {
            //The player has hidden the scoreboard. Show the scoreboard.
            hiddenPlayers.remove(uuid);
        } else {
            //The player has shown the scoreboard. Hide the scoreboard.
            hiddenPlayers.add(uuid);
        }
    }

    public boolean isHidden(final UUID uuid) {
        return hiddenPlayers.contains(uuid);
    }

    public void setHidden(final UUID uuid, final boolean hidden) {
        if (hidden) {
            if (!hiddenPlayers.contains(uuid)) {
                hiddenPlayers.add(uuid);
            }
        } else {
            hiddenPlayers.remove(uuid);
        }
    }

    private void scheduleTask() {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                isRunning = true;

                //Before starting, check if there are no players on the server.
                //Also check if the plugin is refreshing the online and/or hidden players.
                if (!Bukkit.getOnlinePlayers().isEmpty() && !isRefreshing) {
                    Player player;

                    if (index > onlinePlayers.size() - 1 || index > Bukkit.getOnlinePlayers().size() - 1) {
                        index = 0;
                        return;
                    }

                    //The player at hand
                    player = onlinePlayers.get(index);

                    //Set the title.
                    title = instance.getUtils().colorizeAndTranslate(title, player);

                    //Set up the scoreboard
                    BPlayerBoard board = Netherboard.instance().createBoard(player, "PhantomBoard");

                    //If the player has toggled the scoreboard off, then just continue
                    if (hiddenPlayers.contains(player.getUniqueId())) {
                        final BPlayerBoard currentBoard = Netherboard.instance().getBoard(player);
                        if (currentBoard != null) {
                            if (currentBoard.getName().equals(title)) {
                                currentBoard.delete();
                            }
                        }
                    } else {
                        //Set the title
                        board.setName(title);

                        //Set each line.
                        int currentLine = lines.size();
                        for (String line : lines) {
                            if (line.length() > 40) {
                                board.set(instance.getUtils().colorize(instance.getMessages().get("scoreboard-over-40-chars", "&c&nLine is over the limit of 40 chars!")), currentLine);
                            } else {
                                board.set(instance.getUtils().colorizeAndTranslate(line, player), currentLine);
                            }
                            currentLine--;
                        }
                    }
                    index++;
                }
            }
        }.runTaskTimer(instance, 0L, period);
    }
}
