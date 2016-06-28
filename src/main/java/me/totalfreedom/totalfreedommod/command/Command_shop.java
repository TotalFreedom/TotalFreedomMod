package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopData;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Shop commands", usage = "/<command> <list | buy <ID>>", aliases = "sh")
public class Command_shop extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled!", ChatColor.RED);
        }
        final String prefix = FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");
        ShopData sd = plugin.sh.getData(playerSender);
        int coins = sd.getCoins();
        int coloredChatPrice = ConfigEntry.SHOP_COLORED_CHAT_PRICE.getInteger();
        int amountNeeded;
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                msg("-====== ITEMS ======-", ChatColor.GREEN);
                msg(ChatColor.GOLD + "Key: If you can offord the item the price will be " + ChatColor.DARK_GREEN + "green");
                msg(ChatColor.GOLD + "If you can't offord the item the price will be " + ChatColor.DARK_RED + "red");
                msg(ChatColor.GOLD + "If you already have the item the price will be labeled " + ChatColor.RED + "PURCHACED");
                msg(ChatColor.AQUA + "Colored Chat (1) - Cost: " + (sd.isColoredchat() ? ChatColor.RED + "PURCHACED" : (canOfford(coloredChatPrice, coins) ? ChatColor.DARK_GREEN : ChatColor.RED) + ConfigEntry.SHOP_COLORED_CHAT_PRICE.getInteger().toString()));
                return true;
            }
        }
        if (args.length > 1)
        {
            if (args[0].equalsIgnoreCase("buy"))
            {
                switch (args[1])
                {
                    case "1":
                        if (!sd.isColoredchat())
                        {
                            if (canOfford(coloredChatPrice, coins))
                            {
                                sd.setCoins(sd.getCoins() - coloredChatPrice);
                                sd.setColoredchat(true);
                                plugin.sh.save(sd);
                                msg(prefix + ChatColor.GREEN + "Succesfully bought colored chat!");
                                return true;
                            }
                            else
                            {
                                amountNeeded = coloredChatPrice - coins;
                                msg(prefix + ChatColor.RED + "You can not offord colored chat you need " + amountNeeded + " more coins!");
                                return true;
                            }
                        }
                        else
                        {
                            msg(prefix + ChatColor.RED + "You already have colored chat!");
                            return true;
                        }
                    default:
                        msg(prefix + ChatColor.RED + "No item can be found with an ID of " + args[1] + "!");
                        return true;
                }
            }
        }
        return false;
    }
    public boolean canOfford(int p, int c)
    {
        if (c >= p)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
