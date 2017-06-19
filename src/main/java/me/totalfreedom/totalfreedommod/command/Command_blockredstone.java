package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Blocks redstone use on the server.", usage = "/<command>", aliases = "bre")
public class Command_blockredstone extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (ConfigEntry.ALLOW_REDSTONE.getBoolean())
        {
            ConfigEntry.ALLOW_REDSTONE.setBoolean(false);
            FUtil.adminAction(sender.getName(), "Blocking all redstone", true);
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (!ConfigEntry.ALLOW_REDSTONE.getBoolean())
                    {
                        FUtil.adminAction("TotalFreedom", "Unblocking all redstone", false);
                        ConfigEntry.ALLOW_REDSTONE.setBoolean(true);
                    }
                }
            }.runTaskLater(plugin, 300 * 20L);
        }
        else
        {
            ConfigEntry.ALLOW_REDSTONE.setBoolean(true);
            FUtil.adminAction(sender.getName(), "Unblocking all redstone", true);
        }

        return true;
    }
}
