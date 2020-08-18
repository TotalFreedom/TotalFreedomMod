package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans the specified player.", usage = "/<command> <username> [reason] [-nrb | -q]", aliases = "gtfo")
public class Command_ban extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String reason = null;
        Boolean silent = false;
        Boolean cancelRollback = false;
        if (args.length >= 2)
        {
            if (args[args.length - 1].equalsIgnoreCase("-nrb") || args[args.length - 1].equalsIgnoreCase("-q"))
            {
                if (args[args.length - 1].equalsIgnoreCase("-nrb"))
                {
                    cancelRollback = true;
                }

                if (args[args.length - 1].equalsIgnoreCase("-q"))
                {
                    silent = true;
                }

                if (args.length >= 3)
                {
                    reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
                }
            }
            else
            {
                reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
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
            //ips.addAll(entry.getIps());/
            ips.add(FUtil.getIp(player));

            // Deop
            player.setOp(false);

            // Gamemode survival
            player.setGameMode(GameMode.SURVIVAL);

            // Clear inventory
            player.getInventory().clear();

            if (!silent)
            {
                // Strike with lightning
                final Location targetPos = player.getLocation();
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                        targetPos.getWorld().strikeLightning(strike_pos);
                    }
                }
            }
            else
            {
                msg("Banned " + player.getName() + " quietly.");
            }
            // Kill player
            player.setHealth(0.0);
        }

        // Checks if CoreProtect is loaded and installed, and skips the rollback and uses CoreProtect directly
        if (!cancelRollback)
        {
            plugin.cpb.rollback(username);
        }

        if (player != null && !silent)
        {
            FUtil.bcastMsg(player.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);
        }

        // Ban player
        Ban ban;

        if (player != null)
        {
            ban = Ban.forPlayer(player, sender, null, reason);
        }
        else
        {
            ban = Ban.forPlayerName(username, sender, null, reason);
        }

        for (String ip : ips)
        {
            ban.addIp(ip);
            ban.addIp(FUtil.getFuzzyIp(ip));
        }
        plugin.bm.addBan(ban);


        if (!silent)
        {
            // Broadcast
            final StringBuilder bcast = new StringBuilder()
                    .append("Banning: ")
                    .append(username);
            if (reason != null)
            {
                bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
            }
            msg(sender, ChatColor.GRAY + username + " has been banned and IP is: " + StringUtils.join(ips, ", "));
            FUtil.staffAction(sender.getName(), String.format(bcast.toString()), true);
        }

        // Kick player and handle others on IP
        if (player != null)
        {
            player.kickPlayer(ban.bakeKickMessage());
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (FUtil.getIp(p).equals(FUtil.getIp(player)))
                {
                    p.kickPlayer(ChatColor.RED + "You've been kicked because someone on your IP has been banned.");
                }
            }
        }

        // Log ban
        plugin.pul.logPunishment(new Punishment(username, ips.get(0), sender.getName(), PunishmentType.BAN, reason));

        return true;
    }
}
