package me.totalfreedom.totalfreedommod.command;

import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Kicks everyone and stops the server.", usage = "/<command> [reason]")
public class Command_stop extends FreedomCommand
{
    private static final Map<CommandSender, String> STOP_CONFIRM = new HashMap<>();

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        String reason = "Server is going offline, come back in about 20 seconds.";

        if (args.length != 0)
        {
            reason = StringUtils.join(args, " ");
        }

        if (sender.getName().equals("CONSOLE"))
        {
            shutdown(reason);
            return true;
        }
        else if (STOP_CONFIRM.containsKey(sender))
        {
            shutdown(STOP_CONFIRM.get(sender));
            return true;
        }


        msg("Warning: You're about to stop the server. Type /stop again to confirm you want to do this.");

        STOP_CONFIRM.put(sender, reason);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (STOP_CONFIRM.containsKey(sender))
                {
                    STOP_CONFIRM.remove(sender);
                    msg("Stop request expired.");
                }
            }
        }.runTaskLater(plugin, 15 * 20);
        return true;
    }

    public void shutdown(String reason)
    {
        FUtil.bcastMsg("Server is going offline!", ChatColor.LIGHT_PURPLE);

        for (Player player : server.getOnlinePlayers())
        {
            player.kickPlayer(ChatColor.LIGHT_PURPLE + reason);
        }

        STOP_CONFIRM.remove(sender);

        server.shutdown();
    }
}
