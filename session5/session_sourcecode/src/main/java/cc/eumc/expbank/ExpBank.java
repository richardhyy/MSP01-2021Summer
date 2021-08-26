package cc.eumc.expbank;

import cc.eumc.expbank.command.UserCommand;
import cc.eumc.expbank.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExpBank extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register UserCommands
        UserCommand userCommand = new UserCommand(this);
        getCommand("expbank").setExecutor(userCommand);
        getCommand("expbank").setTabCompleter(userCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
