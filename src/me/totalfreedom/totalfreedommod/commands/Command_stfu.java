package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Mutes a player with brute force.", usage = "/<command> [<player> [-s] | list | purge | all]", aliases = "mute")
public class Command_stfu extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            playerMsg("Muted players:");
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isMuted())
                {
                    playerMsg("- " + mp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg("- none");
            }
        }
        else if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unmuting all players.", true);
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            playerMsg("Unmuted " + count + " players.");
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            FUtil.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            FPlayer playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    playerdata = plugin.pl.getPlayer(player);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            playerMsg("Muted " + counter + " players.");
        }
        else
        {
            final Player player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            FPlayer playerdata = plugin.pl.getPlayer(player);
            if (playerdata.isMuted())
            {
                FUtil.adminAction(sender.getName(), "Unmuting " + player.getName(), true);
                playerdata.setMuted(false);
                playerMsg("Unmuted " + player.getName());
            }
            else
            {
                if (!plugin.al.isAdmin(player))
                {
                    FUtil.adminAction(sender.getName(), "Muting " + player.getName(), true);
                    playerdata.setMuted(true);

                    if (args.length == 2 && args[1].equalsIgnoreCase("-s"))
                    {
                        Command_smite.smite(player);
                    }

                    playerMsg("Muted " + player.getName());
                }
                else
                {
                    playerMsg(player.getName() + " is a superadmin, and can't be muted.");
                }
            }
        }

        return true;
    }
}
