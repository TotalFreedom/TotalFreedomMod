package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.Jumppads;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manage jumppads", usage = "/<command> <on | off | info | sideways <on | off> | strength <strength (1-10)>>", aliases = "launchpads,jp")
public class Command_jumppads extends FreedomCommand
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
                playerMsg("Jumppads: " + (plugin.jp.getMode().isOn() ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg("Sideways: " + (plugin.jp.getMode() == Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg("Strength: " + (plugin.jp.getStrength() * 10 - 1), ChatColor.BLUE);
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
                playerMsg("Jumppads are currently disabled, please enable them before changing jumppads settings.");
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
                    playerMsg("Invalid Strength");
                    return true;
                }

                if (strength > 10 || strength < 1)
                {
                    playerMsg("Invalid Strength: The strength may be 1 through 10.");
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
