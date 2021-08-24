package cc.eumc.expbank.command;

import cc.eumc.expbank.ExpBank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserCommand implements CommandExecutor, TabExecutor {
    private final ExpBank plugin;
    private final String[] commands = {"help", "deposit", "withdraw", "borrow", "repay"};

    public UserCommand(ExpBank plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // TODO: 显示帮助
            return true;
        }

        // TODO: 存经验
        // TODO: 取经验
        // TODO: 借经验*
        // TODO: 还经验*
        // TODO: 显示帮助


        return true;
    }


    // TODO: Message-related methods


    // Tab-completer
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1)
            return new ArrayList<>();
        else if (args.length == 1)
            return Arrays.stream(commands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        else
            return Arrays.asList(commands);
    }
}
