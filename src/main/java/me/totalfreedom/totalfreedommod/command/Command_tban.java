package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Temporarily bans a player for five minutes.", usage = "/<command> [-q] <username> [reason]", aliases = "noob")
public class Command_tban extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean quiet = args[0].equalsIgnoreCase("-q");
        if (quiet)
        {
            args = org.apache.commons.lang3.ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        final String username;
        final List<String> ips = new ArrayList<>();

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            final PlayerData entry = plugin.pl.getData(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getName();
            ips.addAll(entry.getIps());
        }
        else
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = player.getName();
            ips.addAll(entry.getIps());
        }

        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        StringBuilder kick = new StringBuilder()
                .append(ChatColor.RED)
                .append("You have been temporarily banned for five minutes. Please read totalfreedom.me for more info.");

        if (!quiet)
        {
            // Strike with lightning
            if (player != null)
            {
                final Location targetPos = player.getLocation();
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                        targetPos.getWorld().strikeLightning(strike_pos);
                    }
                }

                // Kill player
                player.setHealth(0.0);

                if (reason != null)
                {
                    FUtil.staffAction(sender.getName(), "Tempbanning " + player.getName() + " for 5 minutes - Reason: " + reason, true);
                    kick.append("\n")
                            .append(ChatColor.RED)
                            .append("Reason: ")
                            .append(ChatColor.GOLD)
                            .append(reason);
                }
                else
                {
                    FUtil.staffAction(sender.getName(), "Tempbanning " + player.getName() + " for 5 minutes", true);
                }
            }
        }
        else
        {
            if (player != null)
            {
                if (reason != null)
                {
                    msg("Quietly temporarily banned " + player.getName() + " for 5 minutes.");
                    kick.append("\n")
                            .append(ChatColor.RED)
                            .append("Reason: ")
                            .append(ChatColor.GOLD)
                            .append(reason);
                }
            }
        }

        // Ban player
        Ban ban = Ban.forPlayerName(username, sender, FUtil.parseDateOffset("5m"), reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);

        // Kick player
        if (player != null)
        {
            player.kickPlayer(kick.toString());
        }

        // Log ban
        plugin.pul.logPunishment(new Punishment(username, ips.get(0), sender.getName(), PunishmentType.TEMPBAN, reason));
        return true;
    }
}