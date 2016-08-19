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
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> <player> [reason]")
public class Command_smite extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }
        final Player player = this.getPlayer(args[0]);
        String reason = null;
        if (args.length > 1) {
            reason = StringUtils.join((Object[])args, " ", 1, args.length);
        }
        if (player == null) {
            this.msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        smite(player, reason, sender.getName());
        return true;
    }
    
    public static void smite(final Player player) {
        smite(player, null, null);
    }
    
    public static void smite(final Player player, final String reason, String sender) {
        FUtil.bcastMsg(player.getName() + " has been a naughty, naughty boy.", ChatColor.RED);
        if (reason != null && sender != null) {
            FUtil.bcastMsg("  Reason: " + reason, ChatColor.RED);
        }
        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        final Location targetPos = player.getLocation();
        final World world = player.getWorld();
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                final Location strike_pos = new Location(world, (double)(targetPos.getBlockX() + x), (double)targetPos.getBlockY(), (double)(targetPos.getBlockZ() + z));
                world.strikeLightning(strike_pos);
            }
        }
        player.setHealth(0.0);
        if (reason != null && sender != null) {
            player.sendMessage(ChatColor.RED + "You've been smitten. Reason: " + reason);
            player.sendMessage(ChatColor.RED + "Admin: " + sender);
        }
    }
}
