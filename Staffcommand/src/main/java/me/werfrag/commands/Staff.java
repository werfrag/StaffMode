package me.werfrag.commands;

import me.werfrag.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Staff implements CommandExecutor {

    private Connection connection;

    public void StaffModeCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("staffmode.use")) {
                if (args.length == 0) {
                    toggleStaffMode(player);
                } else {
                    player.sendMessage("Utilizzo del comando: /staffmode");
                }
            } else {
                player.sendMessage("§4§lNessun permesso");
            }
        }
        return true;
    }

    private void toggleStaffMode(Player player) {
        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM staffmode WHERE player_uuid = ?");
            selectStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = selectStatement.executeQuery();

            if (!resultSet.next()) {
                // Il giocatore non è presente nel database, salviamo l'inventario e l'armatura e lo aggiungiamo alla tabella
                PlayerInventory playerInventory = player.getInventory();
                ItemStack[] inventoryContents = playerInventory.getContents();
                ItemStack[] armorContents = playerInventory.getArmorContents();

                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO staffmode (player_uuid, inventory_contents, armor_contents) VALUES (?, ?, ?)");
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setBytes(2, Utils.serializeItemStackArray(inventoryContents).getBytes());
                insertStatement.setBytes(3, Utils.serializeItemStackArray(armorContents).getBytes());
                insertStatement.executeUpdate();

                // Cancella l'inventario del giocatore
                playerInventory.clear();

                // Metti oggetti nella hotbar
                playerInventory.setItem(0, createCompassItem());
                playerInventory.setItem(1, createBookItem());
                playerInventory.setItem(7, createFreezeBlockItem());
                playerInventory.setItem(8, createVanishOffItem());

                // Aggiungi il giocatore alla tabella staffmode
                insertStatement = connection.prepareStatement("INSERT INTO staffmode (player_uuid) VALUES (?)");
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.executeUpdate();

                player.sendMessage(ChatColor.YELLOW + "Modalità Staff attivata.");
            } else {
                // Il giocatore è presente nel database, ripristiniamo l'inventario
                ItemStack[] inventoryContents = Utils.deserializeItemStackArray(Arrays.toString(resultSet.getBytes("inventory_contents")));

                PlayerInventory playerInventory = player.getInventory();
                playerInventory.setContents(inventoryContents);

                // Rimuoviamo il giocatore dalla tabella e ripristiniamo l'inventario
                PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM staffmode WHERE player_uuid = ?");
                deleteStatement.setString(1, player.getUniqueId().toString());
                deleteStatement.executeUpdate();

                player.sendMessage(ChatColor.YELLOW + "Modalità Staff disattivata.");
            }

            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack createCompassItem() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Random TP");
        compass.setItemMeta(meta);
        return compass;
    }

    private ItemStack createBookItem() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Inventory Viewer");
        book.setItemMeta(meta);
        return book;
    }

    private ItemStack createFreezeBlockItem() {
        ItemStack freezeBlock = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = freezeBlock.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Freeze");
        freezeBlock.setItemMeta(meta);
        return freezeBlock;
    }

    private ItemStack createVanishOffItem() {
        ItemStack vanishOff = new ItemStack(Material.GRAY_DYE);
        ItemMeta meta = vanishOff.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Vanish Off");
        vanishOff.setItemMeta(meta);
        return vanishOff;
    }
}
