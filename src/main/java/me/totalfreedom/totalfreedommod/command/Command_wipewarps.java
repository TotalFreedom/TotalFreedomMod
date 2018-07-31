package me.totalfreedom.totalfreedommod.command;

import com.earth2me.essentials.Essentials;
import java.io.File;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Removes Essentials warps", usage = "/<command>")
public class Command_wipewarps extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.esb.isEnabled())
        {
            msg("Essentials is not enabled on this server.");
            return true;
        }

        Essentials essentials = plugin.esb.getEssentialsPlugin();
        File warps = new File(essentials.getDataFolder(), "warps");
        FUtil.adminAction(sender.getName(), "Wiping Essentials warps", true);
        FUtil.deleteFolder(warps);
        warps.mkdir();
        essentials.reload();
        msg("All warps deleted.");
        return true;
    }
}
