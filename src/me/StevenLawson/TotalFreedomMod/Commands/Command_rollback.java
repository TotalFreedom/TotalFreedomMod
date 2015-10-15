package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | undo [partialname] purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends TFM_Command {

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0 || args.length > 2) {
            return false;
        }

        if (args.length == 1) {
            String name = args[0];
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                name = player.getName();
            }
            TFM_Util.adminAction(sender.getName(), "Rolling back " + name + ".", true);
            Bukkit.dispatchCommand(sender, "co rb t:1d u:" + name + " r:#global #silent");
            playerMsg("If this rollback was a mistake, use /rollback undo " + name + " within 40 seconds to reverse the rollback.");
            return true;
        }

        if (args.length == 2) {
            if ("undo".equalsIgnoreCase(args[0])) {
                String name = args[1];
                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    name = player.getName();
                }
                TFM_Util.adminAction(sender.getName(), "Reverting rollback of " + name + ".", true);
                Bukkit.dispatchCommand(sender, "co rs t:1d u:" + name + " r:#global #silent");
                return true;
            }
        }

        return false;
    }
}
