package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_WorldEditBridge;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Superadmin Command - For when those people need more than just a ban.", usage = "/<command> <player>", aliases = "demo")
public class Command_demolish extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, final String[] args, boolean senderIsConsole)
    {
        //This command is specifically designed for specific circumstances...
        //Although, we all know that the admins are gonna use it 24/7 anyway, regardless of what its for.
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
        
        //BlockCMD Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);

        playerdata.setCommandsBlocked(!playerdata.allCommandsBlocked());

        TFM_Util.adminAction(sender.getName(), (playerdata.allCommandsBlocked() ? "B" : "Unb") + "locking all commands for " + p.getName(), true);
        playerMsg((playerdata.allCommandsBlocked() ? "B" : "Unb") + "locked all commands.");
            }
        }, 60L); // 3 seconds
        
        //Cage Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);

        Material cage_material_outer = Material.GLASS;
        Material cage_material_inner = Material.AIR;
        if (args.length >= 2)
        {
            if (TFM_Util.isStopCommand(args[1]))
            {
                TFM_Util.adminAction(sender.getName(), "Uncaging " + p.getName() + ".", true);

                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();

            }
            else
            {
                cage_material_outer = Material.matchMaterial(args[1]);
                if (cage_material_outer == null)
                {
                    cage_material_outer = Material.GLASS;
                }
            }
        }
        
        Location target_pos = p.getLocation().add(0, 1, 0);
        playerdata.setCaged(true, target_pos, cage_material_outer, cage_material_inner);
        playerdata.regenerateHistory();
        playerdata.clearHistory();
        TFM_Util.buildHistory(target_pos, 2, playerdata);
        TFM_Util.generateCube(target_pos, 2, playerdata.getCageMaterial(TFM_PlayerData.CageLayer.OUTER));
        TFM_Util.generateCube(target_pos, 1, playerdata.getCageMaterial(TFM_PlayerData.CageLayer.INNER));

        p.setGameMode(GameMode.SURVIVAL);

        TFM_Util.adminAction(sender.getName(), "Caging " + p.getName() + ".", true);
            }
        }, 60L); // 3 seconds
        
        //Halt Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
        if (!playerdata.isHalted())
        {
            TFM_Util.adminAction(sender.getName(), "Halting " + p.getName(), true);
            playerdata.setHalted(true);
        }
            }
        }, 60L); // 3 seconds
        
        //Freeze Command (On player, does not work globally)
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                playerMsg(p, "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
            }
        }, 60L); // 3 seconds
        
        //STFU Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
            if (playerdata.isMuted())
            {
                TFM_Util.adminAction(sender.getName(), "Muting " + p.getName(), true);
                playerdata.setMuted(true);
                playerMsg("Muted " + p.getName());
            }
            else
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    TFM_Util.adminAction(sender.getName(), "Muting " + p.getName(), true);
                    playerdata.setMuted(true);
                    playerMsg("Muted " + p.getName());
                }
                else
                {
                    playerMsg(p.getName() + " is a superadmin, and can't be muted.");
                }
            }
            }
        }, 60L); // 3 seconds
        
        //Orbit Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);

        double strength = 10.0;

        if (args.length >= 2)
        {
            if (TFM_Util.isStopCommand(args[1]))
            {
                playerMsg("Stopped orbiting " + p.getName());
                playerdata.stopOrbiting();
            }

            try
            {
                strength = Math.max(1.0, Math.min(150.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException ex)
            {
                playerMsg(ex.getMessage(), ChatColor.RED);
            }
        }

        p.setGameMode(GameMode.SURVIVAL);
        playerdata.startOrbiting(strength);

        p.setVelocity(new Vector(0, strength, 0));
        TFM_Util.adminAction(sender.getName(), "Orbiting " + p.getName() + ".", false);
            }
        }, 60L); //3 Seconds
        
        //Smite Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                {
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
        p.setHealth(0.0);
    }
            }
        }, 60L); // 3 seconds
        
        //GTFO Command
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_Util.bcastMsg(p.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);

        // Undo WorldEdits:
        TFM_WorldEditBridge.getInstance().undo(p, 15);

        // rollback
        TFM_RollbackManager.rollback(p);

        // deop
        p.setOp(false);

        // set gamemode to survival:
        p.setGameMode(GameMode.SURVIVAL);

        // clear inventory:
        p.getInventory().clear();

        // strike with lightning effect:
        final Location target_pos = p.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(target_pos.getWorld(), target_pos.getBlockX() + x, target_pos.getBlockY(), target_pos.getBlockZ() + z);
                target_pos.getWorld().strikeLightning(strike_pos);
            }
        }

        // Ban IP Address:
        String user_ip = p.getAddress().getAddress().getHostAddress();
        String[] ip_parts = user_ip.split("\\.");
        if (ip_parts.length == 4)
        {
            user_ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
        }
        TFM_Util.bcastMsg(String.format("Banning: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
        TFM_ServerInterface.banIP(user_ip, null, null, null);

        // Ban Username:
        TFM_ServerInterface.banUsername(p.getName(), null, null, null);

        // Kick Player:
        p.kickPlayer("GTFO");
            }
        }, 60L); // 3 seconds
        
        return false;
    }
}
