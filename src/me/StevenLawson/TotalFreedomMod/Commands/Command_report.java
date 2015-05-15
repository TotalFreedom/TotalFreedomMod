package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
@CommandParameters(description = "Report a player for admins to see.", usage = "/<command> <player> <reason>")
public class Command_report extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(PLAYER_NOT_FOUND);
            return true;
        }

        if (sender instanceof Player)
        {
            if (player.equals(sender_p))
            {
                playerMsg(ChatColor.RED + "Please, don't try to report yourself.");
                return true;
            }
        }

        if (TFM_AdminList.isSuperAdmin(player))
        {
            playerMsg(ChatColor.RED + "You can not report an admin.");
            return true;
        }

        String report = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        TFM_Util.reportAction(sender_p, player, report);

        playerMsg(ChatColor.GREEN + "Thank you, your report has been successfully logged.");

        return true;
    }
}
