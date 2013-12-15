package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_DisguiseCraftBridge;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Undisguises all players", usage = "/<command>")
public class Command_uall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Plugin disguiseCraft = server.getPluginManager().getPlugin("DisguiseCraft");
        if (disguiseCraft != null)
        {
            TFM_Util.adminAction(sender.getName(), "Undisguising all players", true);
            TFM_DisguiseCraftBridge.undisguiseAllPlayers();
        }
        else
        {
            playerMsg("I apologize, however DisguiseCraft is not enabled/installed on this server.", ChatColor.RED);
        }

        return true;
    }
}
