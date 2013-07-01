package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Show misc. server info.", usage = "/<command>")
public class Command_status extends TFM_Command
{

    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg(String.format("Total Freedom Mod v%s.%s, built %s.", TotalFreedomMod.pluginVersion, TotalFreedomMod.buildNumber, TotalFreedomMod.buildDate), ChatColor.GOLD);
        playerMsg("TotalFreedomMod was created by Madgeek1450 and DarthSalamon.", ChatColor.GOLD);

        playerMsg("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.", ChatColor.YELLOW);

        playerMsg("Loaded worlds:", ChatColor.BLUE);
        int i = 0;
        for (World world : server.getWorlds())
        {
            playerMsg(String.format("World %d: %s - %d players.", i++, world.getName(), world.getPlayers().size()), ChatColor.BLUE);
        }

        return true;
    }
}
