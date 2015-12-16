package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Shows all IPs registered to a player", usage = "/<command> <player>")
public class Command_findip extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {

            playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        playerMsg("Player IPs: " + StringUtils.join(plugin.pl.getData(player).getIps(), ", "));

        return true;
    }
}
