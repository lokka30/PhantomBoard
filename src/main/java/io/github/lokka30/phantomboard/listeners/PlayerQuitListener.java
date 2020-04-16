package io.github.lokka30.phantomboard.listeners;

import io.github.lokka30.phantomboard.PhantomBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private PhantomBoard instance;

    public PlayerQuitListener(final PhantomBoard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        instance.getScoreboardManager().updateHiddenPlayers();
    }
}
