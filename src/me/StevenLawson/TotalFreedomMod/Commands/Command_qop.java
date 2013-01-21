package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.ADMIN_LEVEL;
import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.SOURCE_TYPE_ALLOWED;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_qop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        boolean silent = false;
        if (args.length == 2)
        {
            silent = args[1].equalsIgnoreCase("-s");
        }

        boolean matched_player = false;

        String target_name = args[0].toLowerCase();

        for (Player p : server.getOnlinePlayers())
        {
            if (p.getName().toLowerCase().indexOf(target_name) != -1 || p.getDisplayName().toLowerCase().indexOf(target_name) != -1)
            {
                matched_player = true;

                if (!silent)
                {
                    TFM_Util.adminAction(sender.getName(), "Opping " + p.getName(), false);
                }
                p.setOp(true);
                p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
            }
        }

        if (!matched_player)
        {
            playerMsg("No targets matched.");
        }

        return true;
    }
}
