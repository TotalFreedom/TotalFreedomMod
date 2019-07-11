package me.totalfreedom.totalfreedommod.command;

import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Curse someone", usage = "/<command> <player>")
public class Command_curse extends FreedomCommand
{

    /* The only problem with this is someone can prevent themself from being cursed by declining to download the
       resource pack. However, if they hit yes, then you can curse them whenever you want and they can't stop it unless
       they go into their server settings. and set server resource packs to prompt or disabled */

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!FUtil.isExecutive(sender.getName()))
        {
            return noPerms();
        }

        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        if (plugin.cul.cursedPlayers.containsKey(player))
        {
            msg("Already attempting to cruse!", ChatColor.RED);
            return true;
        }

        player.setResourcePack("http://play.totalfreedom.me/cursed.zip");
        msg("Attempting to curse " + player.getName(), ChatColor.GREEN);
        plugin.cul.cursedPlayers.put(player, playerSender);


        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.al.isAdmin(sender) && FUtil.isExecutive(sender.getName()))
        {
            return FUtil.getPlayerList();
        }
        return Collections.emptyList();
    }
}
