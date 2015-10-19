package me.totalfreedom.totalfreedommod.commands;

import java.io.File;
import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Removes essentials playerdata", usage = "/<command>")
public class Command_wipeuserdata extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!server.getPluginManager().isPluginEnabled("Essentials"))
        {
            playerMsg("Essentials is not enabled on this server");
            return true;
        }

        FUtil.adminAction(sender.getName(), "Wiping Essentials playerdata", true);

        FUtil.deleteFolder(new File(server.getPluginManager().getPlugin("Essentials").getDataFolder(), "userdata"));

        playerMsg("All playerdata deleted.");
        return true;
    }
}
