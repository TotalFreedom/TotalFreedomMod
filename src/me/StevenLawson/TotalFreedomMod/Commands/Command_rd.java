package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_rd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || sender.isOp())
        {
            if (args.length == 1)
            {
                if (Arrays.asList("minecart", "minecarts", "cart", "carts").contains(args[0].toLowerCase()))
                {
                    sender.sendMessage(ChatColor.GRAY + "Removing all projectiles, dropped items, exp. orbs, primed explosives, and minecarts.");
                    sender.sendMessage(ChatColor.GRAY + String.valueOf(TFM_Util.wipeEntities(true, true)) + " enties removed.");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.GRAY + "Removing all projectiles, dropped items, exp. orbs and primed explosives.");
                sender.sendMessage(ChatColor.GRAY + String.valueOf(TFM_Util.wipeEntities(true)) + " enties removed.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
