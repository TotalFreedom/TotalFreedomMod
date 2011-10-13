package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_Cmds_OP implements CommandExecutor
{
    private TotalFreedomMod plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public TFM_Cmds_OP(TotalFreedomMod plugin)
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
                log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s", sender_p.getName(), ChatColor.stripColor(sender_p.getDisplayName()), commandLabel, TFM_Util.implodeStringList(" ", Arrays.asList(args))));
            }
            else
            {
                senderIsConsole = true;
                log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s", sender.getName(), commandLabel, TFM_Util.implodeStringList(" ", Arrays.asList(args))));
            }
            
            if (cmd.getName().equalsIgnoreCase("opme"))
            {
                if (senderIsConsole)
                {
                    sender.sendMessage("This command only works in-game.");
                }
                else
                {
                    if (TFM_Util.isUserSuperadmin(sender, plugin))
                    {
                        TFM_Util.tfm_broadcastMessage(String.format("(%s: Opping %s)", sender.getName(), sender.getName()), ChatColor.GRAY);
                        sender.setOp(true);
                        sender.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                    }
                    else
                    {
                        sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    }
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("opall"))
            {
                if (TFM_Util.isUserSuperadmin(sender, plugin) || senderIsConsole)
                {
                    TFM_Util.tfm_broadcastMessage(String.format("(%s: Opping everyone)", sender.getName()), ChatColor.GRAY);

                    boolean doSetGamemode = false;
                    GameMode targetGamemode = GameMode.CREATIVE;
                    if (args.length != 0)
                    {
                        if (args[0].equals("-c"))
                        {
                            doSetGamemode = true;
                            targetGamemode = GameMode.CREATIVE;
                        }
                        else if (args[0].equals("-s"))
                        {
                            doSetGamemode = true;
                            targetGamemode = GameMode.SURVIVAL;
                        }
                    }

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.setOp(true);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_OP);

                        if (doSetGamemode)
                        {
                            p.setGameMode(targetGamemode);
                        }
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("deopall"))
            {
                if (TFM_Util.isUserSuperadmin(sender, plugin) || senderIsConsole)
                {
                    TFM_Util.tfm_broadcastMessage(String.format("(%s: De-opping everyone)", sender.getName()), ChatColor.GRAY);

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("qop")) //Quick OP
            {
                if (args.length != 1)
                {
                    return false;
                }

                if (sender.isOp() || senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    boolean matched_player = false;
                    for (Player p : Bukkit.matchPlayer(args[0]))
                    {
                        matched_player = true;

                        TFM_Util.tfm_broadcastMessage(String.format("(%s: Opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(true);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                    }
                    if (!matched_player)
                    {
                        sender.sendMessage("No targets matched.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("qdeop")) //Quick De-op
            {
                if (args.length != 1)
                {
                    return false;
                }

                if (sender.isOp() || senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    boolean matched_player = false;
                    for (Player p : Bukkit.matchPlayer(args[0]))
                    {
                        matched_player = true;

                        TFM_Util.tfm_broadcastMessage(String.format("(%s: De-opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                    }
                    if (!matched_player)
                    {
                        sender.sendMessage("No targets matched.");
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
            log.severe("Exception in TFM_Cmds_OP.onCommand(): " + ex.getMessage());
        }
        
        return false;
    }
}
