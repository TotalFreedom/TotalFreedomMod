package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Jumppads;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Manage jumppads", usage = "/<command> <on | off | info | sideways <on | off> | strength <strength (1-10)>>", aliases = "launchpads,jp")
public class Command_jumppads extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("info"))
            {
                playerMsg("Jumppads: " + (TFM_Jumppads.getMode().isOn() ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg("Sideways: " + (TFM_Jumppads.getMode() == TFM_Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg("Strength: " + (TFM_Jumppads.getStrength() * 10 - 1), ChatColor.BLUE);
                return true;
            }

            if ("off".equals(args[0]))
            {
                TFM_Util.adminAction(sender.getName(), "Disabling Jumppads", false);
                TFM_Jumppads.setMode(TFM_Jumppads.JumpPadMode.OFF);
            }
            else
            {
                TFM_Util.adminAction(sender.getName(), "Enabling Jumppads", false);
                TFM_Jumppads.setMode(TFM_Jumppads.JumpPadMode.MADGEEK);
            }
        }
        else
        {
            if (TFM_Jumppads.getMode() == TFM_Jumppads.JumpPadMode.OFF)
            {
                playerMsg("Jumppads are currently disabled, please enable them before changing jumppads settings.");
                return true;
            }

            if (args[0].equalsIgnoreCase("sideways"))
            {
                if ("off".equals(args[1]))
                {
                    TFM_Util.adminAction(sender.getName(), "Setting Jumppads mode to: Madgeek", false);
                    TFM_Jumppads.setMode(TFM_Jumppads.JumpPadMode.MADGEEK);
                }
                else
                {
                    TFM_Util.adminAction(sender.getName(), "Setting Jumppads mode to: Normal and Sideways", false);
                    TFM_Jumppads.setMode(TFM_Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS);
                }
            }
            else if (args[0].equalsIgnoreCase("strength"))
            {
                final float strength;
                try
                {
                    strength = Float.parseFloat(args[1]);
                }
                catch (NumberFormatException ex)
                {
                    playerMsg("Invalid Strength");
                    return true;
                }

                if (strength > 10 || strength < 1)
                {
                    playerMsg("Invalid Strength: The strength may be 1 through 10.");
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Setting Jumppads strength to: " + String.valueOf(strength), false);
                TFM_Jumppads.setStrength((strength / 10) + 0.1F);
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
