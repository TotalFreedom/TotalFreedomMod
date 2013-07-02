package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Sets everyone's Worldedit block modification limit to 500.", usage = "/<command>")
public class Command_setl extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        //server.dispatchCommand(sender, "wildcard gcmd ? /limit 500"); - Madgeek: No, no, hell no.

        TFM_Util.adminAction(sender.getName(), "Setting everyone's Worldedit block modification limit to 500.", true);

        for (final Player p : server.getOnlinePlayers())
        {
            final boolean isOp = p.isOp();

            if (!isOp)
            {
                p.setOp(true);
            }

            server.dispatchCommand(p, "/limit 500");

            if (!isOp)
            {
                server.getScheduler().runTaskLater(plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        p.setOp(false);
                    }
                }, 20L);
            }
        }

        return true;
    }
}
