package me.StevenLawson.TotalFreedomMod.Commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_UuidManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
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

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        final StringBuilder message = new StringBuilder("Temporarily banned " + player.getName());

        Date expires = TFM_Util.parseDateOffset("30m");
        if (args.length >= 2)
        {
            Date parsed_offset = TFM_Util.parseDateOffset(args[1]);
            if (parsed_offset != null)
            {
                expires = parsed_offset;
            }
        }
        message.append(" until ").append(date_format.format(expires));

        String reason = "Banned by " + sender.getName();
        if (args.length >= 3)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            message.append(", Reason: \"").append(reason).append("\"");
        }

        // strike with lightning effect:
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }

        TFM_Util.adminAction(sender.getName(), message.toString(), true);

        TFM_BanManager.addIpBan(new TFM_Ban(TFM_Util.getIp(player), player.getName(), sender.getName(), expires, reason));
        TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(player), player.getName(), sender.getName(), expires, reason));

        player.kickPlayer(sender.getName() + " - " + message.toString());

        return true;
    }
}
