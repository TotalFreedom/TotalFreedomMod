package me.totalfreedom.totalfreedommod.command;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Ride on the top of the specified player.", usage = "/<command> <playername | mode <normal | off | ask>>")
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

            if (requester.getWorld() != playerSender.getWorld())
            {
                requester.teleport(playerSender);
            }

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

        if (args.length >= 2)
        {
            if (args[0].equalsIgnoreCase("mode"))
            {
                if (args[1].equalsIgnoreCase("normal") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("ask"))
                {
                    PlayerData playerDataSender = plugin.pl.getData(playerSender);
                    playerDataSender.setRideMode(args[1].toLowerCase());
                    plugin.pl.save(playerDataSender);
                    msg("Ride mode is now set to " + args[1].toLowerCase() + ".");
                    return true;
                }
            }
        }

        final Player player = getPlayer(args[0], true);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        final PlayerData playerData = plugin.pl.getData(player);

        if (player == playerSender)
        {
            msg("You can't ride yourself. smh.", ChatColor.RED);
            return true;
        }

        if (playerData.getRideMode().equals("off") && !isStaff(sender))
        {
            msg("That player cannot be ridden.", ChatColor.RED);
            return true;
        }

        if (playerData.getRideMode().equals("ask") && !FUtil.isExecutive(playerSender.getName()))
        {
            msg("Sent a request to the player.", ChatColor.GREEN);
            player.sendMessage(ChatColor.AQUA + sender.getName() + " has requested to ride you.");
            player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.GREEN + "/ride accept" + ChatColor.AQUA + " to allow the player to ride you.");
            player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.RED + "/ride deny" + ChatColor.AQUA + " to deny the player permission.");
            player.sendMessage(ChatColor.AQUA + "Request will expire after 30 seconds.");
            RIDE_REQUESTS.put(player, playerSender);
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    if (!RIDE_REQUESTS.containsKey(player))
                    {
                        return;
                    }
                    RIDE_REQUESTS.remove(player);
                    msg("Request expired.", ChatColor.RED);
                    player.sendMessage(ChatColor.RED + "Request expired.");
                }
            }, 30000);
            return true;
        }

        if (player.getWorld() != playerSender.getWorld())
        {
            playerSender.teleport(player);
        }

        player.addPassenger(playerSender);
        msg(player, playerSender.getName() + " is now riding you, run /eject to eject them.");
        return true;
    }
}