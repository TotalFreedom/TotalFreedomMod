package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
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
    private final List<String> locations = Arrays.asList("Sofa", "Car", "Bed", "Kitchen", "Garage", "Basement", "Home Study");
    private Map<CommandSender, String> featureCooldown = new HashMap<>();

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
        if (args.length == 1)
        {
            if (featureCooldown.containsKey(sender) && featureCooldown.containsValue(args[0]))
            {
                msg("You're on cooldown for this feature.", ChatColor.RED);
                return true;
            }
            Random r = new Random();
            switch (args[0])
            {
                case "daily":
                {
                    sd.setCoins(sd.getCoins() + 100);
                    plugin.sh.save(sd);
                    msg(prefix + ChatColor.GREEN + "You received your 100 coins!");
                    cooldown(86400, args[0]);
                    return true;
                }
                case "search":
                {
                    int amount = FUtil.random(5, 10);
                    String location = locations.get(r.nextInt(locations.size()));
                    sd.setCoins(sd.getCoins() + amount);
                    plugin.sh.save(sd);
                    msg(prefix + ChatColor.AQUA + location + ChatColor.GREEN + " - Found " + ChatColor.RED + amount + ChatColor.GREEN + " coins!");
                    cooldown(30, args[0]);
                    return true;
                }
                default:
                {
                    return false;
                }
            }
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

    private void cooldown(int seconds, String feature)
    {
        featureCooldown.put(sender, feature);
        FreedomCommandExecutor.timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                featureCooldown.remove(sender);
            }
        }, seconds * 1000);
    }

}
