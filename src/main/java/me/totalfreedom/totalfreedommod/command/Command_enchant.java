package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Enchant items.", usage = "/<command> <list | addall | reset | add <name> [level] | remove <name>>")
public class Command_enchant extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        ItemStack item = playerSender.getEquipment().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR)
        {
            msg("You have to hold an item to enchant it");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            boolean has_enchantments = false;

            StringBuilder possible_ench = new StringBuilder("Possible enchantments for held item: ");
            for (Enchantment ench : Enchantment.values())
            {
                if (ench.canEnchantItem(item))
                {
                    has_enchantments = true;
                    possible_ench.append(ench.getName()).append(", ");
                }
            }

            if (has_enchantments)
            {
                msg(possible_ench.toString());
            }
            else
            {
                msg("The held item has no enchantments.");
            }
        }
        else if (args[0].equalsIgnoreCase("addall"))
        {
            for (Enchantment ench : Enchantment.values())
            {
                try
                {
                    if (ench.canEnchantItem(item))
                    {
                        item.addEnchantment(ench, ench.getMaxLevel());
                    }
                }
                catch (Exception ex)
                {
                    msg("Could not add enchantment: " + ench.getName());
                }
            }

            msg("Added all possible enchantments for this item.");
        }
        else if (args[0].equalsIgnoreCase("reset"))
        {
            for (Enchantment ench : item.getEnchantments().keySet())
            {
                item.removeEnchantment(ench);
            }

            msg("Removed all enchantments.");
        }
        else
        {
            if (args.length < 2)
            {
                return false;
            }

            Enchantment ench = null;

            try
            {
                ench = Enchantment.getByName(args[1]);
            }
            catch (Exception ex)
            {
            }

            if (ench == null)
            {
                msg(args[1] + " is an invalid enchantment for the held item. Type \"/enchant list\" for valid enchantments for this item.");
                return true;
            }

            if (args[0].equalsIgnoreCase("add"))
            {
                if (!ench.canEnchantItem(item) && !ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS.getBoolean())
                {
                    msg("Can't use this enchantment on held item.");
                    return true;
                }
                int level = ench.getMaxLevel();
                if (args.length > 2)
                {
                    try
                    {
                        if (ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS.getBoolean())
                        {
                            level = Integer.valueOf(args[2]);
                        }
                        else
                        {
                            level = Math.max(1, Math.min(ench.getMaxLevel(), Integer.parseInt(args[2])));
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("\"" + args[2] + "\" is not a valid number", ChatColor.RED);
                        return true;
                    }
                }
                if (!ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS.getBoolean())
                {
                    item.addEnchantment(ench, level);
                }
                else
                {
                    item.addUnsafeEnchantment(ench, level);
                }

                msg("Added enchantment: " + ench.getName());
            }
            else if (args[0].equals("remove"))
            {
                item.removeEnchantment(ench);

                msg("Removed enchantment: " + ench.getName());
            }
        }

        return true;
    }

    public List<String> getAllEnchantments()
    {
        List<String> enchantments = new ArrayList();
        for (Enchantment enchantment : Enchantment.values())
        {
            enchantments.add(enchantment.getName());
        }
        return enchantments;
    }

    public List<String> getAllEnchantments(ItemStack item)
    {
        List<String> enchantments = new ArrayList();
        for (Enchantment enchantment : Enchantment.values())
        {
            if (enchantment.canEnchantItem(item))
            {
                enchantments.add(enchantment.getName());
            }
        }
        return enchantments;
    }

    public List<String> getEnchantments(ItemStack item)
    {
        List<String> enchantments = new ArrayList();
        for (Enchantment enchantment : item.getEnchantments().keySet())
        {
            enchantments.add(enchantment.getName());
        }
        return enchantments;
    }

    public static List<String> stringNumberRange(int min, int max)
    {
        List<String> range = new ArrayList();
        for (int i = min; i <= max; i++)
        {
            range.add(String.valueOf(i));
        }

        return range;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        Player player;
        if (sender instanceof Player)
        {
            player = (Player)sender;
        }
        else
        {
            return Collections.emptyList();
        }
        ItemStack item = player.getEquipment().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR)
        {
            return Collections.emptyList();
        }

        boolean unsafe = ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS.getBoolean();

        if (args.length == 1)
        {
            return Arrays.asList("list", "addall", "reset", "add", "remove");
        }
        else if (args.length == 2)
        {
            if (args[0].equals("add"))
            {
                if (unsafe)
                {
                    return getAllEnchantments();
                }
                else
                {
                    return getAllEnchantments(item);
                }
            }
            else if (args[0].equals("remove"))
            {
                return getEnchantments(item);
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("add"))
            {
                Enchantment enchantment = Enchantment.getByName(args[1].toUpperCase());
                if (enchantment != null)
                {
                    if (!unsafe)
                    {
                        return stringNumberRange(1, enchantment.getMaxLevel());
                    }
                    else
                    {
                        return Arrays.asList("[level]");
                    }
                }
            }
        }

        return Collections.emptyList();
    }

}
