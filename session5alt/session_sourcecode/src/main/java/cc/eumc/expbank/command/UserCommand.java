package cc.eumc.expbank.command;

import cc.eumc.expbank.ExpBank;
import cc.eumc.expbank.model.PlayerAccount;
import cc.eumc.expbank.util.PlayerExpUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
            sendHelpMessage(sender);
            return true;
        }

        if (sender instanceof Player player) {
            // 玩家执行
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    sendHelpMessage(sender);
                }

                // 取款
                case "withdraw" -> {
                    // 如果有2个参数
                    if (args.length == 2) {
                        // 获取玩家要求存款的数量的文本
                        String amountString = args[1]; // "42"
                        int amount = 0;
                        try {
                            // 解析玩家要求存款的数量。如果格式错误，代码会运行到 catch (Exception ex) 部分
                            amount = Integer.parseInt(amountString); // 42
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NumberFormatError", "!"));
                        }

                        File accountFile = new File(plugin.getAccountFolder(), player.getUniqueId().toString());

                        PlayerAccount playerAccount;
                        try {
                            playerAccount = PlayerAccount.load(accountFile);
                        } catch (Exception e) {
                            if (e instanceof FileNotFoundException) {
                                sendMessage(sender, plugin.getConfig().getString("Message.Failure.AccountNotCreate", "!"));
                            } else {
                                sendMessage(sender, plugin.getConfig().getString("Message.Failure.InternalError", "!"));
                            }
                            return true;
                        }

                        // 获取银行中玩家的余额
                        // ⚠️ 注意：一般情况下不建议在配置文件中存放此种类型的玩家数据
                        // 下次我们将会优化存储方式
                        int balance = playerAccount.getBalance();

                        if (balance < amount) {
                            // 余额不足
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NotEnoughExp", "!"));
                        } else {
                            // balance >= amount
                            // 从银行中扣除对应经验
                            playerAccount.setBalance(balance - amount);
                            // 保存配置文件
                            try {
                                playerAccount.save(accountFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                sendMessage(sender, plugin.getConfig().getString("Message.Failure.InternalError", "!"));
                                return true;
                            }

                            // 给玩家加上经验
                            PlayerExpUtil.changePlayerExp(player, amount);

                            // 成功提示
                            sendMessage(sender, plugin.getConfig().getString("Message.Success.Withdraw", "!"));
                        }

                    } else {
                        // 缺少参数
                        sendMessage(sender, plugin.getConfig().getString("Message.Failure.MissingArgument", "!"));
                    }
                }

                case "deposit" -> {
                    //    /expbank deposit #
                    // 对于同样功能的代码，不再给出注释
                    if (args.length == 2) {
                        String amountString = args[1]; // "42"
                        int amount;

                        // attempt to parse input amount
                        try {
                            amount = Integer.parseInt(amountString); // 42
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NumberFormatError", "!"));
                            return true;
                        }

                        int total = PlayerExpUtil.getPlayerExp(player);

                        if (total < amount) {
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NotEnoughExp", "!"));
                        } else {
                            // total >= amount
                            // 先取出玩家银行余额

                            File accountFile = new File(plugin.getAccountFolder(), player.getUniqueId().toString());

                            PlayerAccount playerAccount;
                            try {
                                playerAccount = PlayerAccount.load(accountFile);
                            } catch (IOException|ClassNotFoundException e) {
                                if (e instanceof FileNotFoundException) {
                                    playerAccount = new PlayerAccount();
                                    sendMessage(sender, plugin.getConfig().getString("Message.Success.CreateAccount", "!"));
                                } else {
                                    e.printStackTrace();
                                    sendMessage(sender, plugin.getConfig().getString("Message.Failure.InternalError", "!"));
                                    return true;
                                }
                            }

                            int balance = playerAccount.getBalance();

                            int oldMaxBalance = playerAccount.getMaxBalance();

                            // 再把要存的经验与余额相加
                            playerAccount.setBalance(balance + amount);
                            // 保存
                            try {
                                playerAccount.save(accountFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                sendMessage(sender, plugin.getConfig().getString("Message.Failure.InternalError", "!"));
                                return true;
                            }

                            // 扣除对应经验
                            PlayerExpUtil.changePlayerExp(player, -amount);

                            // if success?
                            String successMessage = plugin.getConfig().getString("Message.Success.Deposit", "!");
                            sendMessage(sender, successMessage);


                            if (oldMaxBalance < playerAccount.getBalance() && amount >= 10) {
                                Economy economy = plugin.getEconomy();
                                double bonus = amount / 10 * plugin.getConfig().getDouble("Settings.Bonus.Amount", 0);
                                if (bonus != 0) {
                                    economy.depositPlayer(player, bonus);
                                    sendMessage(sender, plugin.getConfig().getString("Message.Bonus", "!") + bonus);
                                }
                            }
                        }
                    } else {
                        sendMessage(sender, plugin.getConfig().getString("Message.Failure.MissingArgument", "!"));
                    }
                }

                default -> {
                    // 如果上述都不符合
                    sendMessage(sender, plugin.getConfig().getString("Message.Failure.InvalidCommand", "!"));
                }
            }

        } else {
            // 控制台执行
            sendMessage(sender, "You must be a player.");
        }

        return true;
    }


    private void sendHelpMessage(CommandSender receiver) {
        sendMessage(receiver, """
                &ahelp:&r 显示帮助
                &adeposit:&r 存经验
                &awithdraw:&r 取经验""");
    }

    private void sendMessage(CommandSender receiver, String message) {
        // 把 & 替换成 §
        receiver.sendMessage(message.replace('&', '§'));
    }

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
