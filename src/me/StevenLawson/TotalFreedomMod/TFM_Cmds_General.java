package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

//TFM_Cmds_General:
//creative
//survival
//status
//radar
//mp
//rd

public class TFM_Cmds_General implements CommandExecutor
{
    private TotalFreedomMod plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public TFM_Cmds_General(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        try
        {
            Player sender_p = null;
            boolean senderIsConsole = false;
            if (sender instanceof Player)
            {
                sender_p = (Player) sender;
                log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s", sender_p.getName(), ChatColor.stripColor(sender_p.getDisplayName()), commandLabel, plugin.implodeStringList(" ", Arrays.asList(args))));
            }
            else
            {
                senderIsConsole = true;
                log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s", sender.getName(), commandLabel, plugin.implodeStringList(" ", Arrays.asList(args))));
            }
            
            if (cmd.getName().equalsIgnoreCase("creative"))
            {
                if (senderIsConsole)
                {
                    if (args.length == 0)
                    {
                        sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                        return true;
                    }
                }
                else
                {
                    if (!sender.isOp())
                    {
                        sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                        return true;
                    }
                }

                Player p;
                if (args.length == 0)
                {
                    p = Bukkit.getPlayerExact(sender.getName());
                }
                else
                {
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage("Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                }

                sender.sendMessage("Setting " + p.getName() + " to game mode 'Creative'.");
                p.sendMessage(sender.getName() + " set your game mode to 'Creative'.");
                p.setGameMode(GameMode.CREATIVE);

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("survival"))
            {
                if (senderIsConsole)
                {
                    if (args.length == 0)
                    {
                        sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                        return true;
                    }
                }
                else
                {
                    if (!sender.isOp())
                    {
                        sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                        return true;
                    }
                }

                Player p;
                if (args.length == 0)
                {
                    p = Bukkit.getPlayerExact(sender.getName());
                }
                else
                {
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage("Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                }

                sender.sendMessage("Setting " + p.getName() + " to game mode 'Survival'.");
                p.sendMessage(sender.getName() + " set your game mode to 'Survival'.");
                p.setGameMode(GameMode.SURVIVAL);

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("status"))
            {
                sender.sendMessage(ChatColor.GRAY + "Server is currently running with 'online-mode=" + (Bukkit.getOnlineMode() ? "true" : "false") + "'.");

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("radar"))
            {
                if (senderIsConsole)
                {
                    sender.sendMessage("This command can only be used in-game.");
                    return true;
                }

                Player sender_player = Bukkit.getPlayerExact(sender.getName());
                Location sender_pos = sender_player.getLocation();
                String sender_world = sender_player.getWorld().getName();

                List<TFM_RadarData> radar_data = new ArrayList<TFM_RadarData>();

                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (sender_world.equals(p.getWorld().getName()) && !p.getName().equals(sender.getName()))
                    {
                        radar_data.add(new TFM_RadarData(p, sender_pos.distance(p.getLocation()), p.getLocation()));
                    }
                }

                Collections.sort(radar_data, new TFM_RadarData());

                sender.sendMessage(ChatColor.YELLOW + "People nearby in " + sender_world + ":");

                int countmax = 5;
                if (args.length == 1)
                {
                    countmax = Integer.parseInt(args[0]);
                }

                int count = 0;
                for (TFM_RadarData i : radar_data)
                {
                    if (count++ > countmax)
                    {
                        break;
                    }

                    sender.sendMessage(ChatColor.YELLOW + String.format("%s - %d, Disguised: %s",
                            i.player.getName(),
                            Math.round(i.distance),
                            MobDisguiseAPI.isDisguised(i.player) ? "Yes" : "No"
                            ));
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("rd"))
            {
                if (senderIsConsole || sender.isOp())
                {
                    sender.sendMessage(ChatColor.GRAY + "Removing all dropped items, arrows, exp. orbs and TNT...");
                    sender.sendMessage(ChatColor.GRAY + String.valueOf(plugin.wipeDropEntities()) + " dropped enties removed.");
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("mp"))
            {
                if (senderIsConsole || sender.isOp())
                {
                    sender.sendMessage(ChatColor.GRAY + "Purging all mobs...");

                    int removed = 0;
                    for (World world : Bukkit.getWorlds())
                    {
                        for (Entity ent : world.getEntities())
                        {
                            if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime)
                            {
                                ent.remove();
                                removed++;
                            }
                        }
                    }

                    sender.sendMessage(ChatColor.GRAY + String.valueOf(removed) + " mobs removed.");
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
			else if (cmd.getName().equalsIgnoreCase("flatlands"))
			{
				Bukkit.getServer().dispatchCommand(sender, "mv tp flatlands");
				return true;
			}
			else if (cmd.getName().equalsIgnoreCase("skylands"))
			{
				Bukkit.getServer().dispatchCommand(sender, "mv tp skylands");
				return true;
			}
        }
        catch (Exception ex)
        {
            log.severe("Exception in TFM_Cmds_General.onCommand(): " + ex.getMessage());
        }
        
        return false;
    }
}
