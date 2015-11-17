package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "It shows senior administrators with a role!", usage = "/<command>", aliases = "al")
public class Command_adminlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        sender.sendMessage(ChatColor.GOLD + "===============( + )===============");
        sender.sendMessage(ChatColor.AQUA + "- Administrators without a role can only be viewed by using '/saconfig list' or at http://immafreedom.eu/showteam.php! -");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_RED + "Executives: KM_Galahad, samennis1");
        // sender.sendMessage(ChatColor.DARK_RED + "System Admins: None");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Developers: AwesomePinch, AndySixx, tylerhyperHD, OxLemonxO");
        sender.sendMessage(ChatColor.BLUE + "Owner: GrannyMary48");
        sender.sendMessage(ChatColor.GOLD + "===============( + )===============");
        return true;
    }
}
