package cc.eumc.expbank.command;

import cc.eumc.expbank.ExpBank;
import cc.eumc.expbank.util.PlayerExpUtil;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
        if (sender instanceof Player player) {
            // 玩家执行
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    sendHelpMessage(sender);
                }

                case "withdraw" -> {
                    if (args.length == 2) {
                        // 首先解析取款数量
                        String amountString = args[1];
                        int amount;

                        try {
                            amount = Integer.parseInt(amountString);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NumberFormatError", "!"));
                            return true;
                        }

                        // 获取主手上的物品
                        ItemStack itemInHand = player.getInventory().getItemInMainHand();
                        // 如果是 WRITTEN_BOOK 的话
                        if (itemInHand.getType() == Material.WRITTEN_BOOK) {
                            // 尝试更新存折
                            if (updatePassbook(itemInHand, -amount)) {
                                // 更新成功
                                sendMessage(sender, "&aSucceeded");
                            } else {
                                // 更新失败（有各种情况）
                                // TODO: 用自定义异常来处理不同的情况
                                sendMessage(sender, "&cFailed");
                            }
                        } else {
                            sendMessage(sender, "&eHold your passbook in main hand.");
                        }

                        // 只有当存折被成功更新后才给予经验
                        PlayerExpUtil.changePlayerExp(player, amount);
                    }
                }

                case "deposit" -> {
                    //    /expbank deposit #
                    // 对于同样功能的代码，不再给出注释
                    if (args.length == 2) {
                        String amountString = args[1];
                        int amount;

                        try {
                            amount = Integer.parseInt(amountString);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            sendMessage(sender, plugin.getConfig().getString("Message.Failure.NumberFormatError", "!"));
                            return true;
                        }

                        int total = PlayerExpUtil.getPlayerExp(player);

                        if (total < amount) {
                            sendMessage(sender, "Not enough EXP");
                        } else {
                            // total >= amount
                            ItemStack itemInHand = player.getInventory().getItemInMainHand();
                            if (itemInHand.getType() == Material.WRITTEN_BOOK) {
                                if (updatePassbook(itemInHand, amount)) {
                                    sendMessage(sender, "&aSucceeded");
                                } else {
                                    sendMessage(sender, "&cFailed");
                                    return true;
                                }
                            } else {
                                // 如果玩家没有拿着存折就给他/她一本新的
                                createPassbook(player, amount);
                            }

                            // 只有当存折被成功更新后才扣除经验
                            PlayerExpUtil.changePlayerExp(player, -amount);
                        }
                    }
                }
            }
        } else {
            sender.sendMessage("You must be a player!");
        }

        return true;
    }


    private void createPassbook(Player player, int initAmount) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        List<String> pages = new ArrayList<>();
        pages.add("DATE: " + date + "\nAMOUNT; BALANCE\n+" + initAmount + ";   " + initAmount + "\n##   END OF PAGE   ##");
        giveBook(player, pages, "存折");
    }

    private boolean updatePassbook(ItemStack passbookItemStack, int delta) {
        List<String> pages = getPages(passbookItemStack);
        if (pages != null) {
            if (pages.size() != 0) {
                /*
                DATE: 21-08-25
                AMOUNT; BALANCE
                +12;  42
                -10;  32
                ##   END OF PAGE   ##
                 */
                String lastPage = pages.get(pages.size() - 1);
                String[] lines = lastPage.split("\n");
                if (lines.length >= 4) {
                    String targetLine = lines[lines.length - 2];
                    String[] split = targetLine.split(";");
                    if (split.length == 2) {
                        String balanceStr = split[1].replace(" ", "");
                        int balance;
                        try {
                            balance = Integer.parseInt(balanceStr);
                        } catch (NumberFormatException ex) {
                            return false;
                        }

                        int newBalance = balance + delta;
                        if (newBalance < 0) { // not enough EXP in account
                            return false;
                        }

                        List<String> newPageLines = new ArrayList<>(Arrays.asList(lines));

                        newPageLines.add(lines.length - 1,
                                (delta > 0 ? "+" : "") + delta +
                                        ";  " +
                                        newBalance);

                        List<String> newPages = new ArrayList<>(pages);
                        newPages.set(pages.size() - 1, Strings.join(newPageLines, '\n'));
                        updatePages(passbookItemStack, newPages);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<String> getPages(ItemStack itemStack) {
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            return bookMeta.getPages();
        } else {
            return null;
        }
    }

    private boolean updatePages(ItemStack itemStack, List<String> pages) {
        if (itemStack.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
            return true;
        } else {
            return false;
        }
    }

    private void giveBook(Player player, List<String> pages, String title) {
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle(title);
        bookMeta.setAuthor("ExpBank");
        bookMeta.setPages(pages);
        writtenBook.setItemMeta(bookMeta);

        // Give the player the book
        Inventory inventory = player.getInventory();
        inventory.addItem(writtenBook);
    }


    // Message-related methods

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
