package cc.eumc.expbank;

import cc.eumc.expbank.command.UserCommand;
import cc.eumc.expbank.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExpBank extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        reloadConfig();

        // 注册玩家监听器（⚠️ 和命令的实现无关）
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


        // 注册 /expbank 命令
        UserCommand userCommand = new UserCommand(this);
        getCommand("expbank").setExecutor(userCommand);
        getCommand("expbank").setTabCompleter(userCommand);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
