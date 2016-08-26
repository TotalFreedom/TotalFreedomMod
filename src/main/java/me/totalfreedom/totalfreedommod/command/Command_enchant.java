package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Enchant items.", usage = "/<command> <list | addall | reset | add <name> | remove <name>>")
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
                if (ench.canEnchantItem(item))
                {
                    item.addEnchantment(ench, ench.getMaxLevel());

                    msg("Added enchantment: " + ench.getName());
                }
                else
                {
                    msg("Can't use this enchantment on held item.");
                }
            }
            else if (args[0].equals("remove"))
            {
                item.removeEnchantment(ench);

                msg("Removed enchantment: " + ench.getName());
            }
        }

        return true;
    }
}
