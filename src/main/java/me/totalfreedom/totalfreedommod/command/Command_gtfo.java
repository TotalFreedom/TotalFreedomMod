package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import static me.totalfreedom.totalfreedommod.util.FUtil.playerMsg;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans a player", usage = "/<command> <username> [reason] [-nrb]", aliases = "ban")
public class Command_gtfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
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

            username = entry.getUsername();
            ips.addAll(entry.getIps());
        }
        else
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = player.getName();
            //ips.addAll(entry.getIps());
            ips.add(Ips.getIp(player));

            // Deop
            player.setOp(false);

            // Gamemode survival
            player.setGameMode(GameMode.SURVIVAL);

            // Clear inventory
            player.getInventory().clear();

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

            // Kill player
            player.setHealth(0.0);
        }

        String reason = null;
        Boolean cancelRollback = false;
        Boolean epicFail = false;
        if (args.length >= 2)
        {
            if (args[args.length - 1].equalsIgnoreCase("-nrb"))
            {
                cancelRollback = true;
                if (args.length >= 3)
                {
                    reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
                }
            }
            if (args[args.length - 1].equalsIgnoreCase("-ef"))
            {
                epicFail =  true;
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

        // Checks if CoreProtect is loaded and installed, and skips the rollback and uses CoreProtect directly
        if (!cancelRollback)
        {
            if (!plugin.cpb.isEnabled())
            {
                // Undo WorldEdits
                try
                {
                    plugin.web.undo(player, 15);
                }
                catch (NoClassDefFoundError | NullPointerException ex)
                {
                }

                // Rollback
                plugin.rb.rollback(username);
            }
            else
            {
                plugin.cpb.rollback(username);
            }
        }

        if (epicFail)
        {
            for (int i = 0; i < 25; i++)
            {
                player.setVelocity(player.getVelocity().clone().add(new Vector(0, 50, 0)));
                new BukkitRunnable()
                {
                    public void run()
                    {
                        for (int i = 0; i < 8; i++)
                        {
                            player.getWorld().strikeLightning(player.getLocation());
                            //FUtil.
                        }
                    }
                }.runTaskLater(plugin, 2L * 20L);
            }
            return true;
        }

        if (player != null)
        {
            FUtil.bcastMsg(player.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);
        }

        // Ban player
        Ban ban = Ban.forPlayerName(username, sender, null, reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);

        // Broadcast
        final StringBuilder bcast = new StringBuilder()
                .append(ChatColor.RED)
                .append(sender.getName())
                .append(" - ")
                .append("Banning: ")
                .append(username);
        if (reason != null)
        {
            bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
        }
        playerMsg(sender, ChatColor.GRAY + username + " has been banned and IP is: " + StringUtils.join(ips, ", "));
        FUtil.bcastMsg(bcast.toString());

        // Kick player and handle others on IP
        if (player != null)
        {
            player.kickPlayer(ban.bakeKickMessage(Ips.getIp(player)));
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (Ips.getIp(p).equals(Ips.getIp(player)))
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