package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "For the imposters.", usage = "/<command> <partialname>")
public class Command_imposter extends TFM_Command
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

        TFM_Util.bcastMsg(player.getName() + " is an imposter.", ChatColor.RED);
        TFM_Util.bcastMsg(player.getName() + " looks like a complete prat. Hah, jokes on you.", ChatColor.RED);
        TFM_Util.bcastMsg(" Banning" + player.getName(), ChatColor.RED);

        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }

        String user_ip = player.getAddress().getAddress().getHostAddress();
        String[] ip_parts = user_ip.split("\\.");
        if (ip_parts.length == 4)
        {
            user_ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
        }
        TFM_Util.bcastMsg(String.format("Banning: %s, IP: %s.", player.getName(), user_ip), ChatColor.RED);
        TFM_ServerInterface.banIP(user_ip, null, null);

        player.kickPlayer(ChatColor.RED + "Your are an imposter, better luck next time! " + ChatColor.WHITE);

        return true;
    }
}
