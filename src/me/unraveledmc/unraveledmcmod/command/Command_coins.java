package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows the amount of coins you have or another player", usage = "/<command> [playername]")
public class Command_coins extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled!", ChatColor.RED);
            return true;
        }

        final String prefix = FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");
        
        if (args.length == 0)
        {
            if (senderIsConsole)
            {
                msg(prefix + ChatColor.RED + "You are not a player, use /coins <playername>");
                return true;
            }
        }
        
        ShopData sd = plugin.sh.getData(playerSender);
        String playerName = sender.getName();

        if (args.length > 0)
        {
            Player p = getPlayer(args[0]);
            
            if (p == null)
            {
                playerName = args[0];
                sd = plugin.sh.getData(args[0]);
            }
            else
            {
                playerName = p.getName();
                sd = plugin.sh.getData(p);
            }
        }

        if (sd == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }
        
        msg(prefix + ChatColor.GREEN + (args.length > 0 ? playerName + " has " : "You have ") + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
        return true;
    }
}
