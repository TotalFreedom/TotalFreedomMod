package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you Crescent Rose", usage = "/<command>", aliases = "cr")
public class Command_crescentrose extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    	ShopData sd = plugin.sh.getData(playerSender);
    	if (!sd.isCrescentRose())
    	{
    		msg("You have not yet purchased Crescent Rose from the shop!", ChatColor.RED);
    		return true;
    	}
    	playerSender.getInventory().addItem(plugin.cr.getCrescentRose());
    	msg("You have been given Crescent Rose!", ChatColor.GREEN);
        return true;
    }
}
