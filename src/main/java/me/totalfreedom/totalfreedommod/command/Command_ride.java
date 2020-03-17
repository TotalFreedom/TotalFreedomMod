package me.totalfreedom.totalfreedommod.command;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make other people your bitch.", usage = "/<command> <playername | mode <normal | off | ask>>")
public class Command_ride extends FreedomCommand
{
    private final Map<Player, Player> RIDE_REQUESTS = new HashMap<>(); // requested, requester
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
        if (fPlayer.getCageData().isCaged())
        {
            msg("You cannot used this command while caged.");
            return true;
        }

        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("yes"))
        {
            if (!RIDE_REQUESTS.containsKey(playerSender))
            {
                msg("You don't have a request currently.");
                return true;
            }
            Player requester = RIDE_REQUESTS.get(playerSender);
            if (requester == null)
            {
                msg("The player who sent the request is no longer online.");
                RIDE_REQUESTS.remove(playerSender);
                return true;
            }
            msg("Request accepted.");
            requester.sendMessage(ChatColor.GRAY + "Your request has been accepted.");
            playerSender.addPassenger(requester);
            return true;
        }

        if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("no"))
        {
            if (!RIDE_REQUESTS.containsKey(playerSender))
            {
                msg("You don't have a request currently.");
                return true;
            }
            Player requester = RIDE_REQUESTS.get(playerSender);
            if (requester == null)
            {
                msg("The player who sent the request is no longer online.");
                RIDE_REQUESTS.remove(playerSender);
                return true;
            }
            msg("Request denied.");
            RIDE_REQUESTS.remove(playerSender);
            requester.sendMessage(ChatColor.GRAY + "Your request has been denied.");
            return true;
        }

        if (args[0].equalsIgnoreCase("mode"))
        {
            if (args[1].equalsIgnoreCase("normal") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("ask"))
            {
                VPlayer vPlayerSender = plugin.pv.getVerificationPlayer(playerSender);
                vPlayerSender.setRideMode(args[1].toLowerCase());
                msg("Ride mode is now set to " + args[1].toLowerCase() + ".");
                return true;
            }
        }

        final Player player = getPlayer(args[0]);
        if (player == null || Command_vanish.VANISHED.contains(player) && !plugin.al.isAdmin(sender))
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        final VPlayer vPlayer = plugin.pv.getVerificationPlayer(player);

        if (player == playerSender)
        {
            msg("You can't ride yourself. smh.", ChatColor.RED);
            return true;
        }

        if (vPlayer.getRideMode().equals("off") && !isAdmin(sender))
        {
            msg("That player cannot be ridden.", ChatColor.RED);
            return true;
        }

        if (vPlayer.getRideMode().equals("ask") && !isAdmin(sender))
        {
            msg("Sent a request to the player.");
            player.sendMessage(ChatColor.GRAY + sender.getName() + " has requested to ride you.");
            player.sendMessage(ChatColor.GRAY + "Type " + ChatColor.DARK_GRAY + "/ride accept" + ChatColor.GRAY + " to allow the player to ride you.");
            player.sendMessage(ChatColor.GRAY + "Type " + ChatColor.DARK_GRAY + "/ride deny" + ChatColor.GRAY + " to deny the player permission.");
            player.sendMessage(ChatColor.GRAY + "Request will expire after 30 seconds.");
            RIDE_REQUESTS.put(player, playerSender);
            FreedomCommandExecutor.timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    if (!RIDE_REQUESTS.containsKey(player))
                    {
                        return;
                    }
                    RIDE_REQUESTS.remove(player);
                    msg("Request expired.");
                    player.sendMessage(ChatColor.GRAY + "Request expired.");
                }
            }, 30000);
            return true;
        }

        if (player.getWorld() != playerSender.getWorld())
        {
            msg("Player is in another world. (" + player.getWorld().getName() + ")");
            return true;
        }
        else
        {
            Location loc = player.getLocation();
            playerSender.teleport(new Location(loc.getWorld(),loc.getX(), loc.getY(), loc.getZ()));
        }

        player.addPassenger(playerSender);

        return true;
    }
}
