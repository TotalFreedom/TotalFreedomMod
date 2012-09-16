package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// readded by JeromSar
public class Command_smite extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_Util.isUserSuperadmin(sender))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        if (args.length != 1)
        {
            return false;
        }

        Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        TFM_Util.bcastMsg(p.getName() + " has been a naughty, naughty boy.", ChatColor.RED);

        //Deop
        p.setOp(false);

        //Set gamemode to survival:
        p.setGameMode(GameMode.SURVIVAL);

        //Clear inventory:
        p.getInventory().clear();

        //Strike with lightning effect:
        final Location target_pos = p.getLocation();
        final World world = p.getWorld();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(world, target_pos.getBlockX() + x, target_pos.getBlockY(), target_pos.getBlockZ() + z);
                world.strikeLightning(strike_pos);
            }
        }

        //Kill:
        p.setHealth(0);

        return true;
    }
}
