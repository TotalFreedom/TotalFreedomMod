package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TFM_Cmds_Admin implements CommandExecutor
{
    private TotalFreedomMod plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public TFM_Cmds_Admin(TotalFreedomMod plugin)
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
            
            if (cmd.getName().equalsIgnoreCase("fr"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length == 0)
                    {
                        plugin.allPlayersFrozen = !plugin.allPlayersFrozen;

                        if (plugin.allPlayersFrozen)
                        {
                            plugin.allPlayersFrozen = true;
                            sender.sendMessage("Players are now frozen.");
                            TFM_Util.tfm_broadcastMessage(sender.getName() + " has temporarily frozen everyone on the server.", ChatColor.AQUA);
                        }
                        else
                        {
                            plugin.allPlayersFrozen = false;
                            sender.sendMessage("Players are now free to move.");
                            TFM_Util.tfm_broadcastMessage(sender.getName() + " has unfrozen everyone.", ChatColor.AQUA);
                        }
                    }
                    else
                    {
                        Player p;
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

                        TFM_UserInfo playerdata = plugin.userinfo.get(p);
                        if (playerdata != null)
                        {
                            playerdata.setFrozen(!playerdata.isFrozen());
                        }
                        else
                        {
                            playerdata = new TFM_UserInfo();
                            playerdata.setFrozen(true);
                            plugin.userinfo.put(p, playerdata);
                        }

                        sender.sendMessage(ChatColor.AQUA + p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                        p.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("gtfo"))
            {
                if (args.length != 1)
                {
                    return false;
                }

                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    Player p;
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

                    TFM_Util.tfm_broadcastMessage(p.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);
                    
                    //Undo WorldEdits:
                    Bukkit.getServer().dispatchCommand(sender, String.format("/undo %d %s", 15, p.getName()));

                    //Deop
                    p.setOp(false);

                    //Set gamemode to survival:
                    p.setGameMode(GameMode.SURVIVAL);

                    //Clear inventory:
                    p.getInventory().clear();

                    //Strike with lightning effect:
                    final Location target_pos = p.getLocation();
                    for (int x = -1; x <= 1; x++)
                    {
                        for (int z = -1; z <= 1; z++)
                        {
                            final Location strike_pos = new Location(target_pos.getWorld(), target_pos.getBlockX() + x, target_pos.getBlockY(), target_pos.getBlockZ() + z);
                            target_pos.getWorld().strikeLightning(strike_pos);
                        }
                    }

                    //Ban IP Address:
                    String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                    TFM_Util.tfm_broadcastMessage(String.format("Banning: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
                    Bukkit.banIP(user_ip);

                    //Ban Username:
                    Bukkit.getOfflinePlayer(p.getName()).setBanned(true);

                    //Kick Player:
                    p.kickPlayer("GTFO");
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("gadmin"))
            {
                if (args.length == 0)
                {
                    return false;
                }

                String mode = args[0].toLowerCase();

                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (mode.equals("list"))
                    {
                        sender.sendMessage(ChatColor.GRAY + "[ Real Name ] : [ Display Name ] - Hash:");
                    }

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        String hash = p.getUniqueId().toString().substring(0, 4);
                        if (mode.equals("list"))
                        {
                            sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                                    p.getName(),
                                    ChatColor.stripColor(p.getDisplayName()),
                                    hash));
                        }
                        else if (hash.equalsIgnoreCase(args[1]))
                        {
                            if (mode.equals("kick"))
                            {
                                p.kickPlayer("Kicked by Administrator");
                            }
                            else if (mode.equals("nameban"))
                            {
                                Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                                TFM_Util.tfm_broadcastMessage(String.format("Banning Name: %s.", p.getName()), ChatColor.RED);
                                p.kickPlayer("Username banned by Administrator.");
                            }
                            else if (mode.equals("ipban"))
                            {
                                String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                                TFM_Util.tfm_broadcastMessage(String.format("Banning IP: %s.", p.getName(), user_ip), ChatColor.RED);
                                Bukkit.banIP(user_ip);
                                p.kickPlayer("IP address banned by Administrator.");
                            }
                            else if (mode.equals("ban"))
                            {
                                String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                                TFM_Util.tfm_broadcastMessage(String.format("Banning Name: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
                                Bukkit.banIP(user_ip);
                                Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                                p.kickPlayer("IP and username banned by Administrator.");
                            }
                            else if (mode.equals("op"))
                            {
                                TFM_Util.tfm_broadcastMessage(String.format("(%s: Opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                                p.setOp(false);
                                p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                            }
                            else if (mode.equals("deop"))
                            {
                                TFM_Util.tfm_broadcastMessage(String.format("(%s: De-opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                                p.setOp(false);
                                p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                            }
                            else if (mode.equals("ci"))
                            {
                                p.getInventory().clear();
                            }
                            else if (mode.equals("fr"))
                            {
                                TFM_UserInfo playerdata = plugin.userinfo.get(p);
                                if (playerdata != null)
                                {
                                    playerdata.setFrozen(!playerdata.isFrozen());
                                }
                                else
                                {
                                    playerdata = new TFM_UserInfo();
                                    playerdata.setFrozen(true);
                                    plugin.userinfo.put(p, playerdata);
                                }
                                sender.sendMessage(ChatColor.AQUA + p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                                p.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                            }

                            return true;
                        }
                    }

                    if (!mode.equals("list"))
                    {
                        sender.sendMessage(ChatColor.RED + "Invalid hash.");
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("wildcard"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args[0].equals("wildcard"))
                    {
                        sender.sendMessage("What the hell are you trying to do, you stupid idiot...");
                        return true;
                    }

                    String base_command = TFM_Util.implodeStringList(" ", Arrays.asList(args));

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        String out_command = base_command.replaceAll("\\x3f", p.getName());
                        sender.sendMessage("Running Command: " + out_command);
                        Bukkit.getServer().dispatchCommand(sender, out_command);
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("nonuke"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length < 1)
                    {
                        return false;
                    }

                    if (args.length >= 2)
                    {
                        plugin.nukeMonitorRange = Double.parseDouble(args[1]);
                    }

                    if (args.length >= 3)
                    {
                        plugin.nukeMonitorCountBreak = Integer.parseInt(args[2]);
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.nukeMonitor = true;
                        sender.sendMessage(ChatColor.GRAY + "Nuke monitor is enabled.");
                        sender.sendMessage(ChatColor.GRAY + "Anti-freecam range is set to " + plugin.nukeMonitorRange + " blocks.");
                        sender.sendMessage(ChatColor.GRAY + "Block throttle rate is set to " + plugin.nukeMonitorCountBreak + " blocks destroyed per 5 seconds.");
                    }
                    else
                    {
                        plugin.nukeMonitor = false;
                        sender.sendMessage("Nuke monitor is disabled.");
                    }
                    
//                    FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILE));
//                    config.set("nuke_monitor", plugin.nukeMonitor);
//                    config.set("nuke_monitor_range", plugin.nukeMonitorRange);
//                    config.set("nuke_monitor_count", plugin.nukeMonitorCountBreak);
//                    config.save(new File(plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILE));
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("prelog"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length != 1)
                    {
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("on"))
                    {
                        plugin.preprocessLogEnabled = true;
                        sender.sendMessage("Command preprocess logging is now enabled. This will be spammy in the log.");
                    }
                    else
                    {
                        plugin.preprocessLogEnabled = false;
                        sender.sendMessage("Command preprocess logging is now disabled.");
                    }
                    
//                    FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILE));
//                    config.set("preprocess_log", plugin.preprocessLogEnabled);
//                    config.save(new File(plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILE));
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("cake"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    StringBuilder output = new StringBuilder();
                    Random randomGenerator = new Random();

                    for (String word : TotalFreedomMod.CAKE_LYRICS.split(" "))
                    {
                        String color_code = Integer.toHexString(1 + randomGenerator.nextInt(14));
                        output.append("§").append(color_code).append(word).append(" ");
                    }

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        ItemStack heldItem = new ItemStack(Material.CAKE, 1);
                        p.getInventory().setItem(p.getInventory().firstEmpty(), heldItem);
                    }

                    TFM_Util.tfm_broadcastMessage(output.toString());
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("gcmd"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length < 2)
                    {
                        return false;
                    }
                    
                    Player p;
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage(ChatColor.GRAY + "Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                    
                    String outcommand = "";
                    try
                    {
                        StringBuilder outcommand_bldr = new StringBuilder();
                        for (int i = 1; i < args.length; i++)
                        {
                            outcommand_bldr.append(args[i]).append(" ");
                        }
                        outcommand = outcommand_bldr.toString().trim();
                    }
                    catch (Exception cmdex)
                    {
                        sender.sendMessage(ChatColor.GRAY + "Error building command: " + cmdex.getMessage());
                    }
                    
                    try
                    {
                        sender.sendMessage(ChatColor.GRAY + "Sending command as " + p.getName() + ": " + outcommand);
                        if (Bukkit.getServer().dispatchCommand(p, outcommand))
                        {
                            sender.sendMessage(ChatColor.GRAY + "Command sent.");
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.GRAY + "Unknown error sending command.");
                        }
                    }
                    catch (Exception cmdex)
                    {
                        sender.sendMessage(ChatColor.GRAY + "Error sending command: " + cmdex.getMessage());
                    }
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("qjail"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length < 1)
                    {
                        return false;
                    }
                    
                    Player p;
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage(ChatColor.GRAY +  "Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                    
                    //Deop
                    p.setOp(false);

                    //Set gamemode to survival:
                    p.setGameMode(GameMode.SURVIVAL);

                    //Clear inventory:
                    p.getInventory().clear();

                    //Strike with lightning effect:
                    final Location target_pos = p.getLocation();
                    for (int x = -1; x <= 1; x++)
                    {
                        for (int z = -1; z <= 1; z++)
                        {
                            final Location strike_pos = new Location(target_pos.getWorld(), target_pos.getBlockX() + x, target_pos.getBlockY(), target_pos.getBlockZ() + z);
                            target_pos.getWorld().strikeLightning(strike_pos);
                        }
                    }
                    
                    //Send to jail "mgjail":
                    Bukkit.getServer().dispatchCommand(sender, String.format("tjail %s mgjail", p.getName()));
                    
                    TFM_Util.tfm_broadcastMessage(p.getName() + " has been JAILED!", ChatColor.RED);
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("umd"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (MobDisguiseAPI.isDisguised(p))
                        {
                            p.sendMessage(ChatColor.GRAY + "You have been undisguised by an administrator.");
                        }
                        
                        MobDisguiseAPI.undisguisePlayer(p);
                        MobDisguiseAPI.undisguisePlayerAsPlayer(p, "");
                    }
                    
                    sender.sendMessage(ChatColor.GRAY + "All players have been undisguised.");
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }
                
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("csay"))
            {
                if (senderIsConsole)
                {
                    String sender_name = sender.getName();
                    
                    if (sender_name.equalsIgnoreCase("remotebukkit"))
                    {
                        sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    }
                    
                    sender_name = sender_name.split("-")[0];
                    
                    StringBuilder outmessage_bldr = new StringBuilder();
                    for (int i = 0; i < args.length; i++)
                    {
                        outmessage_bldr.append(args[i]).append(" ");
                    }
                    
                    TFM_Util.tfm_broadcastMessage(String.format("§7[CONSOLE]§f<§c%s§f> %s", sender_name, outmessage_bldr.toString().trim()));
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }
                
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("cage"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length == 0)
                    {
                        return false;
                    }
                    
                    Player p;
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage(ChatColor.GRAY +  "Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                    
                    TFM_UserInfo playerdata = plugin.userinfo.get(p);
                    if (playerdata == null)
                    {
                        playerdata = new TFM_UserInfo();
                        plugin.userinfo.put(p, playerdata);
                    }
                    
                    Material cage_material_outer = Material.GLASS;
                    Material cage_material_inner = Material.AIR;
                    if (args.length >= 2)
                    {
                        if (args[1].equalsIgnoreCase("end") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("stop"))
                        {
                            playerdata.setCaged(false);
                            playerdata.regenerateHistory();
                            playerdata.clearHistory();
                            return true;
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
                    
                    if (args.length >= 3)
                    {
                        if (args[2].equalsIgnoreCase("water"))
                        {
                            cage_material_inner = Material.STATIONARY_WATER;
                        }
                        else if (args[2].equalsIgnoreCase("lava"))
                        {
                            cage_material_inner = Material.STATIONARY_LAVA;
                        }
                    }
                    
                    Location target_pos = p.getLocation().add(0, 1, 0);
                    playerdata.setCaged(true, target_pos, cage_material_outer, cage_material_inner);
                    playerdata.regenerateHistory();
                    playerdata.clearHistory();
                    TFM_Util.buildHistory(target_pos, 2, playerdata);
                    TFM_Util.generateCube(target_pos, 2, playerdata.getCageMaterial(0));
                    TFM_Util.generateCube(target_pos, 1, playerdata.getCageMaterial(1));
                    
                    p.setGameMode(GameMode.SURVIVAL);
                    
                    TFM_Util.tfm_broadcastMessage(sender.getName() + " caged " + p.getName() + "!", ChatColor.YELLOW);
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }
                
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("orbit"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    if (args.length == 0)
                    {
                        return false;
                    }
                    
                    Player p;
                    List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.isEmpty())
                    {
                        sender.sendMessage(ChatColor.GRAY +  "Can't find user " + args[0]);
                        return true;
                    }
                    else
                    {
                        p = matches.get(0);
                    }
                    
                    double strength = 100.0;
                    if (args.length >= 2)
                    {
                        strength = Double.parseDouble(args[1]);
                    }
                    
                    p.setVelocity(new Vector(0, strength, 0));
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
            log.severe("Exception in TFM_Cmds_Admin.onCommand(): " + ex.getMessage());
        }
        
        return false;
    }
}
