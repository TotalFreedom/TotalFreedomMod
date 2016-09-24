package me.StevenLawson.TotalFreedomMod.Commands;

import java.sql.SQLException;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Reports a player for a offence for admins to review.", usage = "/<command> <partialname> <reason>")
public class Command_report extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length == 0)
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
        String Reported = player.getName();
        String Reporter = sender.getName();

        String ban_reason = null;
        if (args.length <= 2)
        {
            return false;
        }
        else if (args.length >= 2)
        {
            ban_reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");

        }

        if (player == sender_p)
        {
            sender.sendMessage(ChatColor.RED + "Don't try to report yourself, idiot.");
            return true;
        }

        for (Player admins : Bukkit.getOnlinePlayers())
        {
            if (TFM_AdminList.isSuperAdmin(admins))
            {
                admins.sendMessage(TFM_Util.colorize("&8[&4TotalFreedomMod System&8] &a" + sender.getName() + " &4has reported &a" + Reported + " - " + player.getAddress().getAddress().getHostAddress() + " &4 for &2" + ban_reason + "&4."));

            }
        }
        player.sendMessage(TFM_Util.colorize("&8[&4TotalFreedomMod System&8] &4Please note that you have been reported for &2" + ban_reason + " &4and that a admin will be reviewing this shortly ."));

        sender.sendMessage(TFM_Util.colorize("&8[&4TotalFreedomMod System&8] &4Your report against &a " + Reported + " - " + player.getAddress().getAddress().getHostAddress() + " &4for &2" + ban_reason + " &4has been recieved and a admin will be reviewing it shortly ."));

        /*
         * SimpleDateFormat sdf = new SimpleDateFormat("dd-M hh:mm");
         * String Time = sdf.format(new Date());
         * Changed to Unix Time Frame. 
         */
        long unixTime = System.currentTimeMillis() / 1000L;
        /* try
         {
         CJFreedomMod.updateDatabase("INSERT INTO reports (Reported, Reporter, ban_reason, Time, Status) VALUES ('" + Reported + "', '" + Reporter + "', '" + ban_reason + "', '" + unixTime + "', 'open');");
         TFM_Log.info("New Report Added by: " + Reporter);
         }
         catch (SQLException ex)
         {
         sender.sendMessage("Error submitting report to Database.");
         TFM_Log.severe(ex);
         }*/

        return true;
    }
}
