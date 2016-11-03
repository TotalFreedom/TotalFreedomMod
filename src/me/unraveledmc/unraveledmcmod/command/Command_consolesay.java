package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Telnet command - Send a chat message with chat formatting over telnet.", usage = "/<command> <message...>", aliases = "csay")
public class Command_consolesay extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            FUtil.bcastMsg(String.format(ChatColor.GRAY + "[CONSOLE]" + ChatColor.WHITE + " <" + ChatColor.RED + "%s" + ChatColor.WHITE + "> %s", sender.getName(), StringUtils.join(args, " ")));
        }
        return true;
    }
}
