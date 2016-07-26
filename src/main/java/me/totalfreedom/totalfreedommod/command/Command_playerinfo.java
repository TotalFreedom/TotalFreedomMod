package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.shop.ShopData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Get info on a player.", usage = "/<command> <name>", aliases = "pi")
public class Command_playerinfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
        if (getPlayer(args[0]) == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        final Player player = getPlayer(args[0]);
        final ShopData sd = plugin.sh.getData(player);
        FPlayer playerdata;
        playerdata = plugin.pl.getPlayer(player);
        if (args.length == 1)
        {
            msg("Name: " + player.getName(), ChatColor.AQUA);
            msg("Opped: " + (player.isOp() ? "§atrue" : "§cfalse"), ChatColor.LIGHT_PURPLE);
            if (player.getGameMode().equals(GameMode.SURVIVAL))
            {
                msg("Gamemode: survival", ChatColor.DARK_BLUE);
            }
            else if (player.getGameMode().equals(GameMode.CREATIVE))
            {
                msg("Gamemode: creative", ChatColor.DARK_BLUE);
            }
            else if (player.getGameMode().equals(GameMode.ADVENTURE))
            {
                msg("Gamemode: adventure", ChatColor.DARK_BLUE);
            }
            else if (player.getGameMode().equals(GameMode.SPECTATOR))
            {
                msg("Gamemode: spectator", ChatColor.DARK_BLUE);
            }
            msg("IPs: " + StringUtils.join(plugin.pl.getData(player).getIps(), ", "), ChatColor.GREEN);
            msg("Rank: " + plugin.rm.getDisplay(player).getColor() + plugin.rm.getDisplay(player).getName(), ChatColor.LIGHT_PURPLE);
            msg("Last command: " + playerdata.getLastCommand());
            msg("Muted: " + (playerdata.isMuted() ? ChatColor.GREEN + "true" : ChatColor.RED + "§cfalse"), ChatColor.DARK_AQUA);
            msg("Commandspy: " + (playerdata.cmdspyEnabled() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"), ChatColor.RED);
            msg("Location: World: " + player.getLocation().getWorld().getName() + " X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: " + player.getLocation().getBlockZ(), ChatColor.WHITE);
            msg("Tag: " + (playerdata.getTag() == null ? "None" : playerdata.getTag()), ChatColor.WHITE);
            msg("Coins: " + sd.getCoins(), ChatColor.DARK_PURPLE);
            msg("Has colored chat: " + (sd.isColoredchat() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"), ChatColor.BLUE);
            msg("Can set custom login messages: " + (plugin.al.isAdmin(player) ? ChatColor.GREEN + "true" : (sd.isCustomLoginMessage() ? ChatColor.GREEN + "true" : "false")));
            msg("Custom login message: " + (plugin.al.isAdmin(player) ? (plugin.al.getAdmin(player).hasLoginMessage() ? ChatColor.AQUA + FUtil.colorize(plugin.al.getAdmin(player).getLoginMessage()) : "None") : sd.getLoginMessage().equals("none") ? "None" : ChatColor.AQUA + FUtil.colorize(sd.getLoginMessage())), ChatColor.GOLD);
            return true;
        }
        return false;
    }
}

