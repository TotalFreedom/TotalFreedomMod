package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> <player> [reason] [-q]")
public class Command_smite extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        String reason = null;
        boolean silent = false;
        if (args.length >= 2)
        {
            if (args[args.length - 1].equalsIgnoreCase("-q"))
            {
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

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        smite(sender, player, reason, silent);

        plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.SMITE, reason));

        return true;
    }

    public static void smite(CommandSender sender, Player player)
    {
        smite(sender, player, null, false);
    }

    public static void smite(CommandSender sender, Player player, String reason)
    {
        smite(sender, player, reason, false);
    }

    public static void smite(CommandSender sender, Player player, String reason, Boolean silent)
    {
        player.sendTitle(ChatColor.RED + "You've been smitten.", ChatColor.YELLOW + "Be sure to follow the rules!", 20, 100, 60);

        if (!silent)
        {
            FUtil.bcastMsg(player.getName() + " has been a naughty, naughty boy.", ChatColor.RED);
            if (reason != null)
            {
                FUtil.bcastMsg("  Reason: " + ChatColor.YELLOW + reason, ChatColor.RED);
            }
            FUtil.bcastMsg("  Smitten by: " + ChatColor.YELLOW + sender.getName(), ChatColor.RED);
        }
        else
        {
            sender.sendMessage(ChatColor.GRAY + "Smitten " + player.getName() + " quietly.");
        }

        // Deop
        player.setOp(false);

        // Set gamemode to survival
        player.setGameMode(GameMode.SURVIVAL);

        // Clear inventory
        player.getInventory().clear();

        // Strike with lightning effect
        final Location targetPos = player.getLocation();
        final World world = player.getWorld();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(world, targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                world.strikeLightning(strike_pos);
            }
        }

        // Kill
        player.setHealth(0.0);

        if (reason != null)
        {
            player.sendMessage(ChatColor.RED + "You've been smitten. Reason: " + ChatColor.YELLOW + reason);
            player.sendTitle(ChatColor.RED + "You've been smitten.", ChatColor.YELLOW + "Reason: " + reason, 20, 100, 60);
        }
    }
}