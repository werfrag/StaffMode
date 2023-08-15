package me.werfrag.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomTP implements Listener {

    @EventHandler
    public void onPlayerInteractrandom(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();

        if (hand == EquipmentSlot.HAND) {
            ItemStack itemInHand = player.getEquipment().getItemInMainHand();
            if (itemInHand != null && itemInHand.getType() == Material.COMPASS) {
                if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Random tp")) {
                    if (player.hasPermission("appleadmin.core.staff")) {
                        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                        int playerCount = onlinePlayers.size();

                        if (playerCount > 1) {
                            onlinePlayers.remove(player);

                            Random random = new Random();
                            Player randomPlayer = onlinePlayers.get(random.nextInt(playerCount - 1));

                            player.teleport(randomPlayer.getLocation());
                            player.sendMessage(ChatColor.GREEN + "Ti sei teletrasportato a " + randomPlayer.getName());
                        } else {
                            player.sendMessage(ChatColor.RED + "Non ci sono altri giocatori online per teletrasportarti.");
                        }
                    }
                }
            }
        }
    }
}
