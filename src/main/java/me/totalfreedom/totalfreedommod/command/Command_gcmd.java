package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Send a command as someone else.", usage = "/<command> <fromname> <outcommand>")
public class Command_gcmd extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        String outCommand = StringUtils.join(args, " ", 1, args.length);

        if (plugin.cb.isCommandBlocked(outCommand, sender))
        {
            return true;
        }

        // Add variables to command
        final HashMap<String, String> playerVars = makePlayerVariables(player);
        for(String key : playerVars.keySet())
        {
            outCommand = outCommand.replace("?" + key, playerVars.get(key));
        }

        try
        {
            msg("Sending command as " + player.getName() + ": " + outCommand);
            if (server.dispatchCommand(player, outCommand))
            {
                msg("Command sent.");
            }
            else
            {
                msg("Unknown error sending command.");
            }
        }
        catch (Throwable ex)
        {
            msg("Error sending command: " + ex.getMessage());
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
