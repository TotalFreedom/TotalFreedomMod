package me.totalfreedom.totalfreedommod.command;

import java.io.File;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Removes essentials playerdata", usage = "/<command>")
public class Command_wipeuserdata extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!server.getPluginManager().isPluginEnabled("Essentials"))
        {
            msg("Essentials is not enabled on this server.");
            return true;
        }

        FUtil.adminAction(sender.getName(), "Wiping Essentials and worlds playerdata", true);

        for (World w : Bukkit.getWorlds())
        {
            if (w.getName().equals(plugin.wm.adminworld.getWorld().getName()))
            {
                continue;
            }

            FUtil.deleteFolder(new File(server.getPluginManager().getPlugin("Essentials").getDataFolder(), "userdata"));
            FUtil.deleteFolder(new File(Bukkit.getServer().getWorld(plugin.wm.flatlands.getName()).getWorldFolder().getName() + "playerdata"));
            FUtil.deleteFolder(new File(Bukkit.getServer().getWorld(plugin.wm.flatlands.getName()).getWorldFolder().getName() + "stats"));
            FUtil.deleteFolder(new File(Bukkit.getServer().getWorld(w.getName()).getWorldFolder().getName() + "stats"));
            FUtil.deleteFolder(new File(Bukkit.getServer().getWorld(w.getName()).getWorldFolder().getName() + "playerdata"));
            msg("All playerdata deleted.");
            return true;
        }
        return false;
    }
}
