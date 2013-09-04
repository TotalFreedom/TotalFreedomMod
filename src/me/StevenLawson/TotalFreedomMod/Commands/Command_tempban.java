package me.StevenLawson.TotalFreedomMod.Commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [playername] [duration] [reason]")
public class Command_tempban extends TFM_Command
{
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player player;
        try
        {
            player = getPlayer(args[0]);
        }
        catch (PlayerNotFoundException ex)
        {
            playerMsg(ex.getMessage(), ChatColor.RED);
            return true;
        }

        StringBuilder bcast_msg = new StringBuilder("Temporarily banned " + player.getName());

        Date ban_duration = TFM_Util.parseDateOffset("30m");
        if (args.length >= 2)
        {
            Date parsed_offset = TFM_Util.parseDateOffset(args[1]);
            if (parsed_offset != null)
            {
                ban_duration = parsed_offset;
            }
        }
        bcast_msg.append(" until ").append(date_format.format(ban_duration));

        String ban_reason = "Banned by " + sender.getName();
        if (args.length >= 3)
        {
            ban_reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            bcast_msg.append(", Reason: \"").append(ban_reason).append("\"");
        }

        TFM_Util.adminAction(sender.getName(), bcast_msg.toString(), true);
        TFM_ServerInterface.banUsername(player.getName(), ban_reason, sender.getName(), ban_duration);
        TFM_ServerInterface.banIP(player.getAddress().getAddress().getHostAddress().trim(), ban_reason, sender.getName(), ban_duration);
        player.kickPlayer(sender.getName() + " - " + bcast_msg.toString());

        return true;
    }
}
