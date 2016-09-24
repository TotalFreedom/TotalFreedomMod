package me.StevenLawson.TotalFreedomMod.Commands

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Allows you to slam a bitch into the ground faster then you can type /doom", usage = "/<command> <partialname>")
public class Command_slam extends TFM_Command
{
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            Player p;
            playerMsg(ex.getMessage(), ChatColor.RED);
            return true;
        }
        Player p;
        if (sender_p != null)
        {
            p.teleport(sender_p);
        }

        if (sender_p != null)
        {
            TotalFreedomMod.server.broadcastMessage(ChatColor.RED + sender_p.getName() + " slammed " + p.getName() + " into the ground using all there might!");
        }
        else
        {
            TotalFreedomMod.server.broadcastMessage(ChatColor.RED + sender.getName() + " slammed " + p.getName() + " into the ground using all there might!");
        }

        p.setOp(false);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();
        Location playerLocation = p.getLocation();
        playerLocation.setY(150.0D);
        p.teleport(playerLocation);
        playerLocation.setY(p.getLocation().getY() - 1.0D);
        p.setHealth(0);
        p.setVelocity(new Vector(0, -10, 0));

        return true;
    }   

}
