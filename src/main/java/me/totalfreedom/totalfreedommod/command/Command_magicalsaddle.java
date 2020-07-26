package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Obtain a magical saddle.", usage = "/<command>")
public class Command_magicalsaddle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (plugin.pl.getData(playerSender).hasItem(ShopItem.MAGICAL_SADDLE))
        {
            playerSender.getInventory().addItem(plugin.sh.getMagicalSaddle());
            msg("You have been given a Magical Saddle", ChatColor.GREEN);
        }
        else
        {
            msg("You do not own a Magical Saddle! Purchase one from the shop.", ChatColor.RED);
        }
        return true;
    }
}