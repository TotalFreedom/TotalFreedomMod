package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopData;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Access the shop", usage = "/<command> <buy> <item>")
public class Command_shop extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled!", ChatColor.RED);
            return true;
        }
        final String prefix = FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");
        ShopData sd = plugin.sh.getData(playerSender);
        if (args.length == 0)
        {
            msg(prefix + ChatColor.GREEN + "Balance: " + ChatColor.RED + sd.getCoins());
            return true;
        }
        if (args.length != 2)
        {
            return false;
        }
        switch (args[0])
        {
            case "buy":
            {
                ShopItem item = ShopItem.findItem(args[1]);
                if (item == null)
                {
                    msg("Invalid item: " + item);
                    return true;
                }
                if (sd.hasItem(item))
                {
                    msg(prefix + ChatColor.GREEN + "You already have that item! To get it, use " + ChatColor.RED + "/shop get " + item.name() + ChatColor.GREEN + "!");
                    return true;
                }
                if (item.getCost() > sd.getCoins())
                {
                    msg(prefix + ChatColor.GREEN + "You don't have enough money for this item!");
                    return true;
                }
                String signature = sd.giveItem(item);
                sd.setCoins(sd.getCoins() - item.getCost());
                plugin.sh.save(sd);
                ItemStack stack = new ItemStack(item.getMaterial(), 1);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(item.getColoredName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY + signature);
                meta.setLore(lore);
                stack.setItemMeta(meta);
                Inventory inv = playerSender.getInventory();
                inv.setItem(inv.firstEmpty(), stack);
                msg(prefix + ChatColor.GREEN + "You bought a " + item.getColoredName() + ChatColor.GREEN + "!");
                return true;
            }
            case "get":
            {
                ShopItem item = ShopItem.findItem(args[1]);
                if (item == null)
                {
                    msg("Invalid item: " + item);
                    return true;
                }
                if (!sd.hasItem(item))
                {
                    msg(prefix + ChatColor.GREEN + "You don't have that item! To buy iy, use " + ChatColor.RED + "/shop buy " + item.name() + ChatColor.GREEN + "!");
                    return true;
                }
                Inventory inv = playerSender.getInventory();
                inv.setItem(inv.firstEmpty(), sd.getItem(item));
                msg(prefix + ChatColor.GREEN + "You got your " + item.getColoredName() + ChatColor.GREEN + "!");
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

}
