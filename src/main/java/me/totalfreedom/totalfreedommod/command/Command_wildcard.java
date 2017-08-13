package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends FreedomCommand
{

    public static final List<String> BLOCKED_COMMANDS = Arrays.asList(
            "wildcard",
            "gtfo",
            "doom",
            "saconfig"
    );

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        Command runCmd = server.getPluginCommand(args[0]);
        if (runCmd == null)
        {
            msg("Unknown command: " + args[0], ChatColor.RED);
            return true;
        }

        if (BLOCKED_COMMANDS.contains(runCmd.getName()))
        {
            msg("Did you really think that was going to work?", ChatColor.RED);
            return true;
        }

        String baseCommand = StringUtils.join(args, " ");

        if (plugin.cb.isCommandBlocked(baseCommand, sender))
        {
            // CommandBlocker handles messages and broadcasts
            return true;
        }

        for (Player player : server.getOnlinePlayers())
        {
            String runCommand = baseCommand;

            // Add variables to command
            HashMap<String, String> playerVars = makePlayerVariables(player);
            for(String key : playerVars.keySet())
            {
                runCommand = runCommand.replace("?" + key, playerVars.get(key));
            }

            msg("Running Command: " + runCommand);
            server.dispatchCommand(sender, runCommand);
        }

        return true;
    }


    private HashMap<String, String> makePlayerVariables(Player player)
    {
        final HashMap<String, String> playerVars = new HashMap<>();

        /* Player general information*/
        playerVars.put("name", player.getName());
        playerVars.put("nick", player.getDisplayName()); // Display Name is what is shown in chat, if the player has a nickname it will show up in getDisplayName()
        playerVars.put("rank", plugin.rm.getRank(player).getName());
        playerVars.put("title", plugin.rm.getDisplay(player).getName());
        playerVars.put("crank", plugin.rm.getRank(player).getColoredName() + ChatColor.RESET); // Colored rank
        playerVars.put("ctitle", plugin.rm.getDisplay(player).getColoredName() + ChatColor.RESET); // Color title

        /* Player location information*/
        playerVars.put("world", player.getLocation().getWorld().getName());
        playerVars.put("x", String.valueOf(player.getLocation().getX())); // Player X displayed as a float
        playerVars.put("y", String.valueOf(player.getLocation().getY()));
        playerVars.put("z", String.valueOf(player.getLocation().getZ()));
        playerVars.put("ix", String.valueOf(player.getLocation().getBlockX())); // Player X displayed as an integer
        playerVars.put("iy", String.valueOf(player.getLocation().getBlockY()));
        playerVars.put("iz", String.valueOf(player.getLocation().getBlockZ()));
        playerVars.put("pitch", String.valueOf(player.getLocation().getPitch())); // Player horizontal direction
        playerVars.put("yaw", String.valueOf(player.getLocation().getYaw())); // Player vertical direction
        return playerVars;
    }
}
