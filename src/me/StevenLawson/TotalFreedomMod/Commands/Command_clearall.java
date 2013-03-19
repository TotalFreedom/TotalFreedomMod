package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, ignore_permissions = false)
public class Command_clearall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        server.dispatchCommand(sender, "rd");
        server.dispatchCommand(sender, "potion clearall");
        server.dispatchCommand(sender, "uall");

        return true;
    }
}
