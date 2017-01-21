package me.totalfreedom.totalfreedommod.command;
 
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
 
@CommandPermissions(level = Rank.CO_FOUNDER, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
@CommandParameters(description = "~~ REUBEN For the bad admins", usage = "/<command> <playername> = fuckoff")
public class Command_mongify extends FreedomCommand
{
 
    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
 
        final Player player = getPlayer(args[0]);
 
        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
 
        FUtil.adminAction(sender.getName(), "Casting mongification over this faggot, " + player.getName(), true);
        FUtil.bcastMsg(player.getName() + " will be completely obliviated with mongification, then mongified!", ChatColor.RED);
 
        final String ip = player.getAddress().getAddress().getHostAddress().trim();
 
 
        Admin admin = getAdmin(player);
        if (admin != null)
        {
            FUtil.adminAction(sender.getName(), "Removing " + player.getName() + " from the superadmin list", true);
            FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the mong list", true);
            plugin.al.removeAdmin(admin);
        }
 
   
        player.setWhitelisted(false);
 
        // Deop
        player.setOp(false);
 
 
 
        Ban ban = Ban.forPlayer(player, sender);
        ban.setReason("&cFUCKOFF YOU ABUSER!");
        for (String playerIp : plugin.pl.getData(player).getIps())
        {
            ban.addIp(playerIp);
        }
        plugin.bm.addBan(ban);
 
        player.setGameMode(GameMode.SURVIVAL);
 
        // Clear inventory
        player.closeInventory();
        player.getInventory().clear();
 
        player.setFireTicks(10000);
 
        player.getWorld().createExplosion(player.getLocation(), 0F, false);
 
        player.setVelocity(player.getVelocity().clone().add(new Vector(0, 20, 0)));
 
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // strike lightning
                player.getWorld().strikeLightning(player.getLocation());
 
                // kill (if not done already)
                player.setHealth(0.0);
            }
        }.runTaskLater(plugin, 2L * 20L);
 
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // ban msg
                FUtil.adminAction(sender.getName(), " FUCK THIS GUY! THIS MONG NEEDS TO DIE! Banning " + player.getName() + ", IP: " + ip, true);
                FUtil.adminAction(sender.getName(), "- MONG MONG MONG MONG MONG MONG MONG MONG MONG MONG MONG LEADER IS, " + player.getName());
 
                // explosion
                player.getWorld().createExplosion(player.getLocation(), 0F, false);
 
                // kick this dumbass
                player.kickPlayer(ChatColor.RED + "FUCKOFF, and get your shit together you mong!!");
            }
        }.runTaskLater(plugin, 3L * 20L);
 
        return true;
    }
}