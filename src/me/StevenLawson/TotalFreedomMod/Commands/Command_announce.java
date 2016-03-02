package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Announce a message to the server as the broadcaster.", usage = "/<command>", aliases = "announcement, announcer")
public class Command_announce extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    
      public static void announce(final Player player)
      {
        String message = StringUtils.join(ArrayUtils.subarray(args, 0, args.length), " ");
        TFM_Util.bcastMsg(TFM_ConfigEntry.ANNOUNCER_PREFIX.getString().replaceAll("&", "§") + message + " (" + sender.getName() + ")");
        return true;
      }
         
    }
}
