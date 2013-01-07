package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.ONLY_CONSOLE, ignore_permissions = false)
public class Command_moblimiter extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TotalFreedomMod.mobLimiterEnabled = true;
        }
        else if (args[0].equalsIgnoreCase("off"))
        {
            TotalFreedomMod.mobLimiterEnabled = false;
        }
        else if (args[0].equalsIgnoreCase("dragon"))
        {
            TotalFreedomMod.mobLimiterDisableDragon = !TotalFreedomMod.mobLimiterDisableDragon;
        }
        else if (args[0].equalsIgnoreCase("giant"))
        {
            TotalFreedomMod.mobLimiterDisableGiant = !TotalFreedomMod.mobLimiterDisableGiant;
        }
        else if (args[0].equalsIgnoreCase("slime"))
        {
            TotalFreedomMod.mobLimiterDisableSlime = !TotalFreedomMod.mobLimiterDisableSlime;
        }
        else if (args[0].equalsIgnoreCase("ghast"))
        {
            TotalFreedomMod.mobLimiterDisableGhast = !TotalFreedomMod.mobLimiterDisableGhast;
        }
        else
        {
            if (args.length < 2)
            {
                return false;
            }

            if (args[0].equalsIgnoreCase("setmax"))
            {
                try
                {
                    TotalFreedomMod.mobLimiterMax = Math.max(0, Math.min(2000, Integer.parseInt(args[1])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }
        }

        if (TotalFreedomMod.mobLimiterEnabled)
        {
            sender.sendMessage("Moblimiter enabled. Maximum mobcount set to: " + TotalFreedomMod.mobLimiterMax + ".");

            playerMsg("Dragon: " + (TotalFreedomMod.mobLimiterDisableDragon ? "disabled" : "enabled") + ".");
            playerMsg("Giant: " + (TotalFreedomMod.mobLimiterDisableGiant ? "disabled" : "enabled") + ".");
            playerMsg("Slime: " + (TotalFreedomMod.mobLimiterDisableSlime ? "disabled" : "enabled") + ".");
            playerMsg("Ghast: " + (TotalFreedomMod.mobLimiterDisableGhast ? "disabled" : "enabled") + ".");
        }
        else
        {
            playerMsg("Moblimiter is disabled. No mob restrictions are in effect.");
        }

        return true;
    }
}
