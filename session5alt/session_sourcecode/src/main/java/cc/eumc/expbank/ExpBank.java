package cc.eumc.expbank;

import cc.eumc.expbank.command.UserCommand;
import cc.eumc.expbank.listener.PlayerListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ExpBank extends JavaPlugin {
    private File accountFolder;
    private Economy economy = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        reloadConfig();

        accountFolder = new File(this.getDataFolder(), "Accounts");
        if (!accountFolder.exists()){
            accountFolder.mkdirs();
        }

        // Setup Economy (Vault)
        // Ref: https://github.com/MilkBowl/VaultAPI
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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

    // Ref: https://github.com/MilkBowl/VaultAPI
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }


    public Economy getEconomy() {
        return economy;
    }

    public File getAccountFolder() {
        return accountFolder;
    }
}
