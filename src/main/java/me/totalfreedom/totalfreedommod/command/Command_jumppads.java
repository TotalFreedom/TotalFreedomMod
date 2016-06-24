package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manage jumppads", usage = "/<command> <on | off | info | sideways <on | off> | strength <strength (1-10)>>", aliases = "launchpads,jp")
public class Command_jumppads extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("info"))
            {
                msg("Jumppads: " + (plugin.jp.getMode().isOn() ? "Enabled" : "Disabled"), ChatColor.BLUE);
                msg("Sideways: " + (plugin.jp.getMode() == Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS ? "Enabled" : "Disabled"), ChatColor.BLUE);
                msg("Strength: " + (plugin.jp.getStrength() * 10 - 1), ChatColor.BLUE);
                return true;
            }

            if ("off".equals(args[0]))
            {
                FUtil.adminAction(sender.getName(), "Disabling Jumppads", false);
                plugin.jp.setMode(Jumppads.JumpPadMode.OFF);
            }
            else
            {
                FUtil.adminAction(sender.getName(), "Enabling Jumppads", false);
                plugin.jp.setMode(Jumppads.JumpPadMode.MADGEEK);
            }
        }
        else
        {
            if (plugin.jp.getMode() == Jumppads.JumpPadMode.OFF)
            {
                msg("Jumppads are currently disabled, please enable them before changing jumppads settings.");
                return true;
            }

            if (args[0].equalsIgnoreCase("sideways"))
            {
                if ("off".equals(args[1]))
                {
                    FUtil.adminAction(sender.getName(), "Setting Jumppads mode to: Madgeek", false);
                    plugin.jp.setMode(Jumppads.JumpPadMode.MADGEEK);
                }
                else
                {
                    FUtil.adminAction(sender.getName(), "Setting Jumppads mode to: Normal and Sideways", false);
                    plugin.jp.setMode(Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS);
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
                    msg("Invalid Strength");
                    return true;
                }

                if (strength > 10 || strength < 1)
                {
                    msg("Invalid Strength: The strength may be 1 through 10.");
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Setting Jumppads strength to: " + String.valueOf(strength), false);
                plugin.jp.setStrength((strength / 10) + 0.1F);
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
