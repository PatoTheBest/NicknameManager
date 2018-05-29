package be.spyproof.nickmanager.listeners;

import be.spyproof.nickmanager.controller.INicknameController;
import be.spyproof.nickmanager.model.NicknameData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

/**
 * Created by Spyproof on 13/11/2016.
 *
 * Listen to players logging in and out.
 */
public class PlayerListener implements Listener
{
    private INicknameController playerController;

    public PlayerListener(INicknameController playerController)
    {
        this.playerController = playerController;
    }

    /**
     * Wrap the player with the player controller, which should save the player to the underlying storage.
     *
     * If the player has a nickname, apply it to the player
     * @param event The fired event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        this.playerController.wrap(event.getUniqueId(), event.getName());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event)
    {
        if(event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            this.playerController.logout(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent event)
    {
        NicknameData nicknameData = this.playerController.wrap(event.getPlayer().getUniqueId(), event.getPlayer().getName());

        if (nicknameData.getNickname().isPresent() && nicknameData.getNickname().isPresent())
            event.getPlayer().setDisplayName(ChatColor.translateAlternateColorCodes('&', nicknameData.getNickname().get()) + ChatColor.RESET);
    }

    /**
     * Trigger the logout method from the player controller.
     * @param event The fired event
     */
    @EventHandler
    public void onLogout(PlayerQuitEvent event)
    {
        this.playerController.logout(event.getPlayer().getUniqueId());
    }
}
