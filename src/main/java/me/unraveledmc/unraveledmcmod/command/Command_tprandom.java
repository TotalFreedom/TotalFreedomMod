package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Random;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto a random place in your current world", usage = "/<command>", aliases = "tpr")
public class Command_tprandom extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Random r = new Random();
        int x = playerSender.getLocation().getBlockX() + r.nextInt(1000);
        int z = playerSender.getLocation().getBlockZ() + r.nextInt(1000);
        Location l = new Location(playerSender.getLocation().getWorld(), x, playerSender.getLocation().getBlockY(), z);
        playerSender.teleport(l);
        msg("Poof!", ChatColor.GREEN);
        return true;
    }
}
