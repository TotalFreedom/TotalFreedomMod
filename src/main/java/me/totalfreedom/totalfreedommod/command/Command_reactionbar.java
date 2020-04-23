package me.totalfreedom.totalfreedommod.command;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Brings the current reaction string up on your action bar", usage = "/<command>")
public class Command_reactionbar extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.sh.reactionString.isEmpty())
        {
            ActionBarAPI.sendActionBar(playerSender, ChatColor.BOLD + plugin.sh.reactionString, 15 * 20);
        }
        return true;
    }
}
