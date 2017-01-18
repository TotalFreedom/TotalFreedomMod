package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you Thor's hammer", usage = "/<command>")
public class Command_thorhammer extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    	ShopData sd = plugin.sh.getData(playerSender);
    	if (!sd.isThorHammer())
    	{
    		msg("You have not yet purchased Thor's hammer from the shop!", ChatColor.RED);
    		return true;
    	}
    	playerSender.getInventory().addItem(plugin.ln.getThorHammer());
    	msg("You have been given Thor's hammer!", ChatColor.GREEN);
        return true;
    }
}
