package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Announce a message!", usage = "/<command> <announcement>")
public class Command_announce extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        
        String announcement = StringUtils.join(args, " ");
        
        TFM_Util.bcastMsg(String.format(ChatColor.DARK_PURPLE + "[" + ChatColor.YELLOW + "Announcement:%s" + ChatColor.DARK_PURPLE + "] %s", sender.getName(), ChatColor.BLUE + announcement), ChatColor.GREEN);

        return true;
    }
}
