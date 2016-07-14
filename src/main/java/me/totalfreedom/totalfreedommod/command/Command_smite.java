package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> <bitch's name> [reason]")
public class Command_smite extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final Player bitch = getPlayer(args[0]);

        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        if (bitch == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        smite(bitch, reason);
        plugin.da.setAdminDeopped(bitch.getName(), true);
        return true;
    }

    public static void smite(Player bitch)
    {
        smite(bitch, null);
    }

    public static void smite(Player bitch, String reason)
    {
        FUtil.bcastMsg(bitch.getName() + " has been a naughty, naughty boy.", ChatColor.RED);

        if (reason != null)
        {
            FUtil.bcastMsg("  Reason: " + reason, ChatColor.RED);
        }

        // Deop
        bitch.setOp(false);

        // Set gamemode to survival
        bitch.setGameMode(GameMode.SURVIVAL);

        // Clear inventory
        bitch.getInventory().clear();

        // Strike with lightning effect
        final Location targetPos = bitch.getLocation();
        final World world = bitch.getWorld();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(world, targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                world.strikeLightning(strike_pos);
            }
        }

        // Kill
        bitch.setHealth(0.0);

        if (reason != null)
        {
            bitch.sendMessage(ChatColor.RED + "You've been smitten. Reason: " + reason);
        }
    }
}
