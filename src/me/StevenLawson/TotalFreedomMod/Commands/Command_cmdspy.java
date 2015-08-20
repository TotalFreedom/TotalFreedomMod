package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Spy on commands", usage = "/<command>", aliases = "commandspy")
public class Command_cmdspy extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {        
        TFM_Admin admin = TFM_AdminList.getEntry(sender_p);
        admin.setCommandSpy(!admin.cmdSpyEnabled());
        playerMsg("CommandSpy " + (admin.cmdSpyEnabled() ? "enabled." : "disabled."));
        return true;
    }
}
