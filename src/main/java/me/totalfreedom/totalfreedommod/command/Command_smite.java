package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> <player> [reason]")
public class Command_smite extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        smite(sender, player, reason);

        plugin.pul.logPunishment(new Punishment(player.getName(), Ips.getIp(player), sender.getName(), PunishmentType.SMITE, reason));

        return true;
    }

    public static void smite(CommandSender sender, Player player)
    {
        smite(sender, player, null);
    }

    public static void smite(CommandSender sender, Player player, String reason)
    {
        FUtil.bcastMsg(player.getName() + " has been a naughty, naughty boy.", ChatColor.RED);

        if (reason != null)
        {
            FUtil.bcastMsg("  Reason: " + ChatColor.YELLOW + reason, ChatColor.RED);
        }
        FUtil.bcastMsg("  Smited by: " + ChatColor.YELLOW + sender.getName(), ChatColor.RED);

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
        }
    }
}
