package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Switch server online-mode on and off.", usage = "/<command> <on | off>")
public class Command_onlinemode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            msg("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.", ChatColor.WHITE);
            msg("\"/onlinemode on\" and \"/onlinemode off\" can be used to change online mode from the console.", ChatColor.WHITE);
        }
        else
        {
            boolean onlineMode;

            if (sender instanceof Player && !plugin.al.isSeniorAdmin(sender))
            {
                noPerms();
                return true;
            }

            if (args[0].equalsIgnoreCase("on"))
            {
                onlineMode = true;
            }
            else if (args[0].equalsIgnoreCase("off"))
            {
                onlineMode = false;
            }
            else
            {
                return false;
            }

            try
            {
                plugin.si.setOnlineMode(onlineMode);

                if (onlineMode)
                {
                    for (Player player : server.getOnlinePlayers())
                    {
                        player.kickPlayer("Server is activating \"online-mode=true\". Please reconnect.");
                    }
                }

                FUtil.adminAction(sender.getName(), "Turning player validation " + (onlineMode ? "on" : "off") + ".", true);

                server.reload();
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return true;
    }
}
