package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import net.dean.jraw.ApiException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Link your reddit account", usage = "/<command> <username | code <code>>")
public class Command_linkreddit extends FreedomCommand
{

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (!plugin.rd.enabled)
        {
            msg("The Reddit system is currently disabled.", ChatColor.RED);
            return true;
        }

        if (getData(playerSender).getRedditUsername() != null)
        {
            msg("Your Reddit account is already linked.");
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args.length == 1 && !args[0].equals("code"))
        {
            String username = args[0];
            String code = plugin.rd.addLinkCode(getData(playerSender), username);

            try
            {
                plugin.rd.sendModMessage(username, "Link Code", "Please run the following in-game to link your Reddit account: /linkreddit code " + code);
            }
            catch (ApiException e)
            {
                msg("Could not find a Reddit account by the name of " + args[0], ChatColor.RED);
                return true;
            }

            msg("A linking code has been sent to " + username + ". Please check your mod mail at " + ChatColor.AQUA + "https://www.reddit.com/message/moderator", ChatColor.GREEN);
            return true;
        }

        String code = args[1];
        String username = plugin.rd.checkLinkCode(code);

        if (username == null)
        {
            msg(code + " is not a valid code", ChatColor.RED);
            return true;
        }

        msg("Successfully linked the Reddit account " + username + " to your Minecraft account.", ChatColor.GREEN);
        if (plugin.rd.updateFlair(playerSender))
        {
            msg("Your flair has been updated.", ChatColor.GREEN);
        }
        return true;
    }
}
