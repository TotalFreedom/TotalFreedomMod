package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Superadmin command - Purge everything! (except for bans).", usage = "/<command>")
public class Command_purgeall extends TFM_Command
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
        server.dispatchCommand(sender, "blockcmd purge");
        server.dispatchCommand(sender, "halt purge");
        
        //If I'm missing any, lemme know. Or just add it yourself.

        return true;
    }
}
