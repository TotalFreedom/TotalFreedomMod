package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigInventory;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "View inventory information of players.", usage = "/<command> <player> <slot>", aliases = "il,invlookup")
public class Command_inventorylookup extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 2)
        {
            return false;
        }
        VPlayer vPlayer = plugin.pv.getVerificationPlayer(args[0]);
        if (vPlayer == null)
        {
            msg("Could not find player or find that player's data.");
            return true;
        }
        ConfigInventory inv = vPlayer.getInventory();
        int slot;
        try
        {
            slot = Integer.valueOf(args[1]);
        }
        catch (NumberFormatException ex)
        {
            msg("That is not a valid number.");
            return true;
        }
        ItemStack stack = inv.get(slot);
        if (stack == null)
        {
            stack = new ItemStack(Material.AIR, 1);
        }
        msg(args[0] + "'s Inventory - Slot " + slot + ":");
        msg(" - Material: " + stack.getType().name());
        msg(" - Amount: " + stack.getAmount());
        if (inv.hasNBT(slot))
        {
            msg(" - NBT Data: " + inv.getNBT(slot).toString());
        }
        return true;
    }
}
