package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.pravian.aero.util.Ips;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Executive things...", usage = "/<command> [hell: <username>]", aliases = "exec")
public class Command_executive extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SERVER_EXECS.getStringList().contains(sender.getName()) && !ConfigEntry.SERVER_OWNERS.getStringList().contains(sender.getName()) && !FUtil.UMCDEVS.contains(sender.getName()))
        {
            msg("You aren't an executive admin, have a cookie instead!");
            if (!senderIsConsole)
            {
                final int firstEmpty = playerSender.getInventory().firstEmpty();
                final ItemStack cakeItem = new ItemStack(Material.CAKE);
                playerSender.getInventory().setItem(firstEmpty, cakeItem);
            }
            else
            {
                msg("Sorry, you're not an in-game player, so it's impossible to give you a cookie :(");
            }
            return true;
        }
	if (args.length > 0)
        {
            switch (args[0])
            {
                case "hell":
                {
                    if (args.length == 2)
                    {
                        if (getPlayer(args[1]) == null)
                        {
                            msg(FreedomCommand.PLAYER_NOT_FOUND);
                            return true;
                        }
                        final Player player = getPlayer(args[1]);
                        String reason = null;

                        FUtil.adminAction(sender.getName(), "Calling Saten to open the gates of hell for " + player.getName(), true);
                        FUtil.bcastMsg(player.getName() + " is going to have a bad time!", ChatColor.RED);
                        final String IP = player.getAddress().getAddress().getHostAddress().trim();
                        if (plugin.al.isAdmin(player))
                        {
                            Admin admin = plugin.al.getAdmin(player);
                            admin.setActive(false);
                            plugin.al.save();
                            plugin.al.updateTables();
                        }
                        player.setWhitelisted(false);
                        player.setOp(false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.closeInventory();
                        player.getInventory().clear();
                        player.setFireTicks(10000);
                        player.getWorld().createExplosion(player.getLocation(), 4F);
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().strikeLightning(player.getLocation());
                            }
                        }.runTaskLater(plugin, 20L * 2L);
                        player.getWorld().createExplosion(player.getLocation(), 4F);
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().strikeLightning(player.getLocation());
                            }
                        }.runTaskLater(plugin, 20L * 2L);
                        FUtil.bcastMsg("The gates to hell have opened, let the wrath of " + sender.getName() + " condem " + player.getName() + "!", ChatColor.RED);
                        player.setFireTicks(10000);
                        final CommandSender cSender = sender;
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().createExplosion(player.getLocation(), 4F);
                                banPlayer(player.getName(), "You are need to get your act together, come back when you can handle yourself!", true, false);
                                FUtil.adminAction(cSender.getName(), "Has sent " + player.getName() + " to hell, IP: " + IP, true);
                                player.kickPlayer(ChatColor.RED + "Welcome to hell you fucking ignorant cunt!");
                            }
                        }.runTaskLater(plugin, 40L * 4L);
                        return true;
                    }
                    else
                    {
                        return false;
                    }    
                }
            }
            return false;
        }
        return false;
    }
    public void banPlayer (String playerName, String reason, Boolean silent, Boolean kick)
    {
        PlayerData playerData = plugin.pl.getData(playerName);
        final List<String> ips = new ArrayList<>();
        ips.addAll(playerData.getIps());
        String username;
        final Player player = getPlayer(args[1]);
        username = playerName;
        if (!silent)
        {
            FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
        }
        Ban ban = Ban.forPlayerName(username, sender, null, reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
            ban.addIp(FUtil.getFuzzyIp(ip));
        }
        plugin.bm.addBan(ban);

        if (player != null && kick)
        {
            player.kickPlayer(ban.bakeKickMessage());
        }
    }
}
