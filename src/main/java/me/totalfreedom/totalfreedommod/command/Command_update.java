package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.Updater;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.rank.Rank;
import java.io.File;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Update UnraveledMCMod", usage = "/<command> <URL>")
public class Command_update extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!sender.getName().equals("CreeperSeth"))
        {
            msg("Only CreeperSeth may execute this command!");
            return true;
        }

        if (args.length > 0)
        {
            final String surl = args[0];
            final String name = sender.getName();
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        FUtil.adminAction(name, "Updating UnraveledMCMod", true);
                        FLog.info("Downloading: " + surl);
                        File file = new File("./plugins/", "UnraveledMCMod.jar");
                        if (file.exists())
                        {
                            file.delete();
                        }
                        if (!file.getParentFile().exists())
                        {
                            file.getParentFile().mkdirs();
                        }

                        Updater.downloadFile(surl, file, true);
                    }
                    catch (Exception ex)
                    {
                        FLog.severe(ex);
                    }
                }
            }.runTaskAsynchronously(plugin);
            return true;
        }
        return false;
    }
}