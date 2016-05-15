package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.totalfreedom.totalfreedommod.player.FPlayer;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Get info on a player.", usage = "/<command> <player>", aliases = "pi")
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
        FPlayer playerdata;
        playerdata = plugin.pl.getPlayer(player);
        if (args.length == 1)
        {
        msg("Name: " + player.getName(), ChatColor.AQUA);
        msg("Opped: " + (player.isOp() ? "§2true" : "§4false"), ChatColor.LIGHT_PURPLE);
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
        msg("IP: " + player.getAddress(), ChatColor.GREEN);
        msg("Rank: " + plugin.rm.getRank(player).getName(), ChatColor.LIGHT_PURPLE);
        msg("Last command: " + playerdata.getLastCommand());
        msg("Muted: " + (playerdata.isMuted() ? "§2true" : "§4false"), ChatColor.DARK_AQUA);
        msg("CommandSpy: " + (playerdata.cmdspyEnabled() ? "§2true" : "§4false"), ChatColor.RED);
        msg("Location: World: " + player.getLocation().getWorld().getName() + " X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: " + player.getLocation().getBlockZ(), ChatColor.WHITE);
        msg(ChatColor.YELLOW + "Tag: " + playerdata.getTag());
        return true;
        }
        return false;
    }
}

