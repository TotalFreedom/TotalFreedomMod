package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Look into another player's inventory, optionally take items out.", usage = "/<command> <player>")
public class Command_invsee extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length != 1)
        {
            msg("You need to specify a player.");
            return false;
        }

        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg("This player is not online.");
            return false;
        }

        if (playerSender == player)
        {
            msg("You cannot invsee yourself.");
            return true;
        }

        if (isAdmin(player) && !isAdmin(playerSender))
        {
            msg("You can't spy on admins!");
            return true;
        }

        playerSender.closeInventory();
        FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
        fPlayer.setInvsee(true);
        Inventory playerInv = player.getInventory();
        playerSender.openInventory(playerInv);    
        return true;
    }

}
