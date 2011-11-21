package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public class Command_mp extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || sender.isOp())
        {
            sender.sendMessage(ChatColor.GRAY + "Purging all mobs...");

            int removed = 0;
            for (World world : Bukkit.getWorlds())
            {
                for (Entity ent : world.getLivingEntities())
                {
                    if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime || ent instanceof EnderDragon)
                    {
                        ent.remove();
                        removed++;
                    }
                }
            }

            sender.sendMessage(ChatColor.GRAY + String.valueOf(removed) + " mobs removed.");
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
