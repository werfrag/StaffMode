package me.werfrag;

import me.werfrag.commands.Staff;
import me.werfrag.events.RandomTP;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Database database;

    @Override
    public void onEnable() {
        System.out.println("§4§l-----------------------------------");
        System.out.println("§f§l       §4§lStaff command      ");
        System.out.println("§4§lPlugin attivato con successo. Admin");
        System.out.println("§4§l-----------------------------------");

        String dbFileName = "database.db"; // Nome del tuo file del database SQLite
        database = new Database(getDataFolder().getPath() + "/" + dbFileName);

        this.getCommand("Staff").setExecutor(new Staff());
        getServer().getPluginManager().registerEvents(new RandomTP(), this);
    }

    @Override
    public void onDisable() {
        System.out.println("§4§l-----------------------------------");
        System.out.println("§f§l       §4§lStaff command     ");
        System.out.println("§4§lPlugin disattivato con successo. SV");
        System.out.println("§4§l-----------------------------------");
        if (database != null) {
            database.disconnect();
        }
    }
}
