package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_WorldEditBridge;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "A humerous replacement for /gtfo", usage = "/<command> <player>")
public class Command_jam extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
        final Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }
        
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_Util.bcastMsg("Hey " + p.getName() + ", what's the difference between jelly and jam?", ChatColor.RED);
            }
        }, 100L); //5 Seconds
        
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_Util.bcastMsg("I can't jelly my banhammer up your ass.", ChatColor.RED);
            }
        }, 100L); //5 Seconds
        
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_Util.adminAction(sender.getName(), "Jamming their banhammer up " + p.getName() + "'s ass...", true);
                
                //Strike lightning
                final Location target_pos = p.getLocation();
                
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        final Location strike_pos = new Location(target_pos.getWorld(), target_pos.getBlockX() + x, target_pos.getBlockY(), target_pos.getBlockZ() + z);
                        target_pos.getWorld().strikeLightning(strike_pos);
                    }
                }
                
                //Kills player, if not done already
                p.setHealth(0);
                
                //Deops
                p.setOp(false);
                
                // Undo WorldEdits:
                TFM_WorldEditBridge.getInstance().undo(p, 15);

                // rollback
                TFM_RollbackManager.rollback(p);
                
                // Clears inventory
                p.getInventory().clear();
                
                // Changes Gamemode
                p.setGameMode(GameMode.SURVIVAL);
                
                // Bans IP Address:
                String user_ip = p.getAddress().getAddress().getHostAddress();
                String[] ip_parts = user_ip.split("\\.");
                if (ip_parts.length == 4)
                {
                    user_ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                }
                TFM_Util.bcastMsg(String.format("Banning: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
                TFM_ServerInterface.banIP(user_ip, null, null, null);

                // Bans Username:
                TFM_ServerInterface.banUsername(p.getName(), null, null, null);
                
                // Kicks The Player
                p.kickPlayer("Nice try.");                
            }
        }, 40L); //2 Seconds
        
        return true;
    }
}
