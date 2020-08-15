package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggles jumppads on/off, view the status of jumppads, or make them sideways.", usage = "/<command> <on | off | info | sideways <on | off>>", aliases = "launchpads,jp")
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
                msg("Jumppads: " + (plugin.jp.players.get(playerSender).isOn() ? "Enabled" : "Disabled"), ChatColor.BLUE);
                msg("Sideways: " + (plugin.jp.players.get(playerSender) == Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS ? "Enabled" : "Disabled"), ChatColor.BLUE);
                return true;
            }

            if ("off".equals(args[0]))
            {
                if(plugin.jp.players.get(playerSender) == Jumppads.JumpPadMode.OFF)
                {
                    msg("Your jumppads are already disabled.");
                    return true;
                }
                msg("Disabled your jumppads.", ChatColor.GRAY);
                plugin.jp.players.put(playerSender, Jumppads.JumpPadMode.OFF);
            }
            else
            {
                if(plugin.jp.players.get(playerSender) != Jumppads.JumpPadMode.OFF)
                {
                    msg("Your jumppads are already enabled.");
                    return true;
                }
                msg("Enabled your jumpppads.", ChatColor.GRAY);
                plugin.jp.players.put(playerSender, Jumppads.JumpPadMode.MADGEEK);
            }
        }
        else
        {
            if (plugin.jp.players.get(playerSender) == Jumppads.JumpPadMode.OFF)
            {
                msg("Your jumppads are currently disabled, please enable them before changing jumppads settings.");
                return true;
            }

            if (args[0].equalsIgnoreCase("sideways"))
            {
                if ("off".equals(args[1]))
                {
                    if(plugin.jp.players.get(playerSender) == Jumppads.JumpPadMode.MADGEEK)
                    {
                        msg("Your jumppads are already set to normal mode.");
                        return true;
                    }
                    msg("Set Jumppads mode to: Normal", ChatColor.GRAY);
                    plugin.jp.players.put(playerSender, Jumppads.JumpPadMode.MADGEEK);
                }
                else
                {
                    if(plugin.jp.players.get(playerSender) == Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS)
                    {
                        msg("Your jumppads are already set to normal and sideways mode.");
                        return true;
                    }
                    msg("Set Jumppads mode to: Normal and Sideways", ChatColor.GRAY);
                    plugin.jp.players.put(playerSender, Jumppads.JumpPadMode.NORMAL_AND_SIDEWAYS);
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.sl.isStaff(sender))
        {
            return Collections.emptyList();
        }
        if (args.length == 1)
        {
            return Arrays.asList("on", "off", "info", "sideways");
        }
        else if (args.length == 2)
        {
            if (args[0].equals("sideways"))
            {
                return Arrays.asList("on", "off");
            }
        }

        return Collections.emptyList();
    }
}