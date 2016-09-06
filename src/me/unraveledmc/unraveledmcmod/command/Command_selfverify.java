package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Enable or disable self verification", usage = "/<command> <on | off>")
public class Command_selfverify extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equals("on"))
            {
                ConfigEntry.VERIFY_ENABLED.setBoolean(true);
                FUtil.adminAction(sender.getName(), "Enabling the self verification system", true);
                return true;
            }
            else if (args[0].equals("off"))
            {
                ConfigEntry.VERIFY_ENABLED.setBoolean(false);
                FUtil.adminAction(sender.getName(), "Disabling the self verification system", true);
                return true;
            }
        }
        return false;
    }
}
