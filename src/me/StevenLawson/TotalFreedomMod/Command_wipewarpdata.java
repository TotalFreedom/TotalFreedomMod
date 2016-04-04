package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Deletes all warps from the server", usage = "/<command>")
public class Command_wipewarpdata extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!server.getPluginManager().isPluginEnabled("Essentials"))
        {
            playerMsg("Essentials is not enabled on this server");
            return true;
        }

        TFM_Util.adminAction(sender.getName(), "Wiping all warps from the server", true);

        TFM_Util.deleteFolder(new File(server.getPluginManager().getPlugin("Essentials").getDataFolder(), "warps"));

        playerMsg("Deleting warps success.");
        return true;
    }
}
