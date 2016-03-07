package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> <playername> [reason]")
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

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        
        else if (args.length > 1)
        {
            String reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
            smite(player, reason);
            return true;
        }
        
        else
        {
            smite(player);
        }

        return true;
    }

    public static void smite(final Player player, final String reason)
    {
        if (reason == null)
        {
            FUtil.bcastMsg(player.getName() + " has been naughty, naughty boy.", ChatColor.RED);
        }

        else
        {
            FUtil.bcastMsg(player.getName() + " has been a naughty, naughty boy.\n" + ChatColor.YELLOW + "Reason: " + reason, ChatColor.RED);
        }
        //Deop
        player.setOp(false);

        //Set gamemode to survival:
        player.setGameMode(GameMode.SURVIVAL);

        //Clear inventory:
        player.getInventory().clear();

        //Strike with lightning effect:
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

        //Kill:
        player.setHealth(0.0);

    }

    public static void smite(final Player player)
    {
        String reason = null;
        smite(player, reason);
    }

}
