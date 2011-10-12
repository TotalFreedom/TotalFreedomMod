package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_Cmds_AntiBlock implements CommandExecutor
{
    private TotalFreedomMod plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public TFM_Cmds_AntiBlock(TotalFreedomMod plugin)
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
            
            if (cmd.getName().equalsIgnoreCase("explosives"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length == 0)
                    {
                        return false;
                    }

                    if (args.length == 2)
                    {
                        plugin.explosiveRadius = Double.parseDouble(args[1]);
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowExplosions = true;
                        sender.sendMessage("Explosives are now enabled, radius set to " + plugin.explosiveRadius + " blocks.");
                    }
                    else
                    {
                        plugin.allowExplosions = false;
                        sender.sendMessage("Explosives are now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("firespread"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowFireSpread = true;
                        sender.sendMessage("Fire spread is now enabled.");
                    }
                    else
                    {
                        plugin.allowFireSpread = false;
                        sender.sendMessage("Fire spread is now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("fireplace"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowFirePlace = true;
                        sender.sendMessage("Fire placement is now enabled.");
                    }
                    else
                    {
                        plugin.allowFirePlace = false;
                        sender.sendMessage("Fire placement is now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("lavadmg"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowLavaDamage = true;
                        sender.sendMessage("Lava damage is now enabled.");
                    }
                    else
                    {
                        plugin.allowLavaDamage = false;
                        sender.sendMessage("Lava damage is now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("lavaplace"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowLavaPlace = true;
                        sender.sendMessage("Lava placement is now enabled.");
                    }
                    else
                    {
                        plugin.allowLavaPlace = false;
                        sender.sendMessage("Lava placement is now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("waterplace"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.allowWaterPlace = true;
                        sender.sendMessage("Water placement is now enabled.");
                    }
                    else
                    {
                        plugin.allowWaterPlace = false;
                        sender.sendMessage("Water placement is now disabled.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
        }
        catch (Exception ex)
        {
            log.severe("Exception in TFM_Cmds_AntiBlock.onCommand(): " + ex.getMessage());
        }
        
        return false;
    }
}
