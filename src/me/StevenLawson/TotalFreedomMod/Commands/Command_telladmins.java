package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Is somebody causing trouble? This command will tell the admins.", usage = "/<command> <playername>")
public class Command_telladmins extends TFM_Command {

    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        final Player p;
        try {
            p = getPlayer(args[0]);
        } catch (CantFindPlayerException ex) {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        if (!TFM_SuperadminList.isUserSuperadmin(p)) {
            TFM_Util.bcastMsg(sender.getName() + " has reported " + p.getName() + ". Could admin teleport to them to see if they still need help.", ChatColor.RED);
        } else {
            playerMsg("The chosen player is a admin noob.", ChatColor.RED);
        }


        return true;
    }
}
