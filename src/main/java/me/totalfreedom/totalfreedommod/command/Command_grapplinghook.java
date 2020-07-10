package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Obtain a grappling hook", usage = "/<command>")
public class Command_grapplinghook extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (plugin.pl.getData(playerSender).hasItem(ShopItem.GRAPPLING_HOOK))
        {
            playerSender.getInventory().addItem(plugin.sh.getGrapplingHook());
            msg("You have been given a Grappling Hook", ChatColor.GREEN);
        }
        else
        {
            msg("You do not own a Grappling Hook! Purchase one from the shop.", ChatColor.RED);
        }
        return true;
    }
}
