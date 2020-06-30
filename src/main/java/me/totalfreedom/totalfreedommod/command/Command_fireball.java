package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Obtain a fire ball", usage = "/<command>")
public class Command_fireball extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (plugin.pl.getData(playerSender).hasItem(ShopItem.FIRE_BALL))
        {
            playerSender.getInventory().addItem(plugin.sh.getFireBall());
            msg("You have been given a Fire Ball", ChatColor.GREEN);
        }
        else
        {
            msg("You do not own a Fire Ball! Purchase one from the shop.", ChatColor.RED);
        }
        return true;
    }
}
