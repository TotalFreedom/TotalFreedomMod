package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone.", usage = "/<command> <purge | off | <partialname> [custom | block] [Block name | Player name(for skull)]")
public class Command_cage extends FreedomCommand
{
    public static String playerSkullName;
    
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }
        if ("off".equals(args[0]) && sender instanceof Player) {
            FUtil.adminAction(sender.getName(), "Uncaging " + sender.getName(), true);
            final FPlayer playerdata = ((TotalFreedomMod)this.plugin).pl.getPlayer(playerSender);
            playerdata.getCageData().setCaged(false);
            return true;
        }
        if ("purge".equals(args[0])) {
            FUtil.adminAction(sender.getName(), "Uncaging all players", true);
            for (final Player player : this.server.getOnlinePlayers()) {
                final FPlayer playerdata2 = ((TotalFreedomMod)this.plugin).pl.getPlayer(player);
                playerdata2.getCageData().setCaged(false);
            }
            return true;
        }
        final Player player2 = this.getPlayer(args[0]);
        if (player2 == null) {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        final FPlayer playerdata3 = ((TotalFreedomMod)this.plugin).pl.getPlayer(player2);
        Material outerMaterial = Material.GLASS;
        Material innerMaterial = Material.AIR;
        if (args.length >= 2 && null != args[1]) {
            final String s = args[1];
            switch (s) {
                case "off": {
                    FUtil.adminAction(sender.getName(), "Uncaging " + player2.getName(), true);
                    playerdata3.getCageData().setCaged(false);
                    return true;
                }
                case "custom": {
                    outerMaterial = Material.SKULL;
                    Command_cage.playerSkullName = args[2];
                    break;
                }
                case "block": {
                    if (Material.matchMaterial(args[2]) != null) {
                        outerMaterial = Material.matchMaterial(args[2]);
                        break;
                    }
                    sender.sendMessage(ChatColor.RED + "Invalid block!");
                    break;
                }
            }
        }
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("water")) {
                innerMaterial = Material.STATIONARY_WATER;
            }
            else if (args[2].equalsIgnoreCase("lava")) {
                innerMaterial = Material.STATIONARY_LAVA;
            }
        }
        final Location targetPos = player2.getLocation().clone().add(0.0, 1.0, 0.0);
        playerdata3.getCageData().cage(targetPos, outerMaterial, innerMaterial);
        player2.setGameMode(GameMode.SURVIVAL);
        if (outerMaterial != Material.SKULL) {
            FUtil.adminAction(sender.getName(), "Caging " + player2.getName(), true);
        }
        else {
            FUtil.adminAction(sender.getName(), "Caging " + player2.getName() + " in " + Command_cage.playerSkullName, true);
        }
        return true;
    }
}
