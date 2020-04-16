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
    private ArrayList<UUID> hiddenPlayers;
    private String title;
    private List<String> lines;
    private int period;
    private boolean isRunning = false;

    public ScoreboardManager(final PhantomBoard instance) {
        this.instance = instance;
    }

    public void load() {
        hiddenPlayers = new ArrayList<>();
        updateHiddenPlayers();

        title = instance.getSettings().get("scoreboard.title", instance.getUtils().colorize("&a&nPhantomBoard"));
        lines = instance.getSettings().get("scoreboard.lines", Collections.singletonList(instance.getUtils().colorize("&7Invalid settings file!")));
        period = instance.getSettings().get("scoreboard.updatePeriod", 20);

        if (!isRunning) {
            scheduleTask();
        }
    }

    public void updateHiddenPlayers() {
        hiddenPlayers.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            final UUID uuid = player.getUniqueId();
            if (instance.getData().get("players." + uuid.toString() + ".hidden", false)) {
                hiddenPlayers.add(uuid);
            }
        }
    }

    public void toggleHidden(final UUID uuid) {
        if (hiddenPlayers.contains(uuid)) {
            //The player has hidden the scoreboard. Show the scoreboard.
            hiddenPlayers.remove(uuid);
        } else {
            //The player has shown the scoreboard. Hide the scoreboard.
            hiddenPlayers.add(uuid);
        }
        writeHiddenPlayersToDisk();
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
        writeHiddenPlayersToDisk();
    }

    private void writeHiddenPlayersToDisk() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String path = "players." + onlinePlayer.getUniqueId().toString() + ".hidden";

            if (hiddenPlayers.contains(onlinePlayer.getUniqueId())) {
                instance.getData().set(path, true);
            } else {
                instance.getData().set(path, false);
            }
        }
    }

    private void updateBoards() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            BPlayerBoard board = Netherboard.instance().createBoard(onlinePlayer, "PhantomBoard");

            if (title.length() > 40) {
                title = instance.getUtils().colorize(instance.getMessages().get("scoreboard-over-40-chars", "&c&nLine is over the limit of 40 chars!"));
            } else {
                title = instance.getUtils().colorizeAndTranslate(title, onlinePlayer);
            }

            //Check if the scoreboard is hidden. Also check if they have perm to view the board.
            if (hiddenPlayers.contains(onlinePlayer.getUniqueId()) || !onlinePlayer.hasPermission("phantomboard.view")) {
                //Get their current scoreboard.
                final BPlayerBoard currentBoard = Netherboard.instance().getBoard(onlinePlayer);

                //Do they have a scoreboard active?
                if (currentBoard != null) {
                    //If their scoreboard is PhantomBoard, then delete it
                    if (currentBoard.getName().equalsIgnoreCase(title)) {
                        currentBoard.delete();
                    }
                }
            } else {
                board.setName(title);

                int currentLine = lines.size();
                for (String line : lines) {
                    line = instance.getUtils().colorizeAndTranslate(line, onlinePlayer);

                    if (line.length() > 40) {
                        board.set(instance.getUtils().colorize(instance.getMessages().get("scoreboard-over-40-chars", "&c&nLine is over the limit of 40 chars!")), currentLine);
                    } else {
                        board.set(line, currentLine);
                    }
                    currentLine--;
                }
            }
        }
    }

    private void scheduleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                isRunning = true;
                updateBoards();
            }
        }.runTaskTimer(instance, 0L, period);
    }
}
