package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Someone being a little bitch? Smite them down...", usage = "/<command> [playername]")
public class Command_smite extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {   
        final Player player = getPlayer(args[0]);
        
        final ArrayList<Player> cooldown = new ArrayList<Player>();
        
        if (cooldown.contains(player))
        {
            playerMsg("That player has been smited recently. Please wait a moment before smiting again.");
            return true;
        }
        if (args.length != 1)
        {
            return false;
        }

        

        if (player == null)
        {
            playerMsg(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        smite(player);
        cooldown.add(player);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TotalFreedomMod.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                cooldown.remove(player);
            }
        }, 100);

        return true;
    }

    public static void smite(final Player player)
    {
        TFM_Util.bcastMsg(player.getName() + " has been a naughty, naughty boy.", ChatColor.RED);

        //Deop
        player.setOp(false);

        //Set gamemode to survival:
        player.setGameMode(GameMode.SURVIVAL);

        //Clear inventory:
        player.getInventory().clear();

        //Strike with lightning effect:
        final Location targetPos = player.getLocation();
        final World world = player.getWorld();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(world, targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                world.strikeLightning(strike_pos);
            }
        }

        //Kill:
        player.setHealth(0.0);
    }
}
