package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "Senioradmin command - Purge everything! (except for bans).", usage = "/<command>")
public class Command_spurgeall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        server.dispatchCommand(sender, "rd");
        server.dispatchCommand(sender, "potion clearall");
        server.dispatchCommand(sender, "uall");
        server.dispatchCommand(sender, "mute purge");
        server.dispatchCommand(sender, "fr purge");
        server.dispatchCommand(sender, "mp");
        server.dispatchCommand(sender, "blockcmd");
        server.dispatchCommand(sender, "halt purge");
        server.dispatchCommand(sender, "lockup purge");
        
        TFM_Util.bcastMsg("[Purge] Purged!", ChatColor.LIGHT_PURPLE);
        //If I'm missing any, lemme know. Or just add it yourself.

        return true;

    }
}
