package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Sends the specified player to their doom.", usage = "/<command> <playername> [reason]")
public class Command_doom extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        FUtil.staffAction(sender.getName(), "Casting oblivion over " + player.getName(), true);
        FUtil.bcastMsg(player.getName() + " will be completely obliviated!", ChatColor.RED);

        final String ip = player.getAddress().getAddress().getHostAddress().trim();

        // Remove from admin
        StaffMember staffMember = getAdmin(player);
        if (staffMember != null)
        {
            FUtil.staffAction(sender.getName(), "Removing " + player.getName() + " from the staff list", true);
            staffMember.setActive(false);
            plugin.sl.save(staffMember);
            plugin.sl.updateTables();
            plugin.amp.updateAccountStatus(staffMember);
            if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
            {
                plugin.dc.syncRoles(staffMember, plugin.pl.getData(staffMember.getName()).getDiscordID());
            }
        }

        // Remove from whitelist
        player.setWhitelisted(false);

        // Deop
        player.setOp(false);

        String reason = null;

        if (args.length > 1)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }

        // Ban player
        Ban ban = Ban.forPlayer(player, sender);
        ban.setReason((reason == null ? "FUCKOFF" : reason));
        for (String playerIp : plugin.pl.getData(player).getIps())
        {
            ban.addIp(playerIp);
        }
        plugin.bm.addBan(ban);

        // Set gamemode to survival
        player.setGameMode(GameMode.SURVIVAL);

        // Clear inventory
        player.closeInventory();
        player.getInventory().clear();

        // Ignite player
        player.setFireTicks(10000);

        // Generate explosion
        player.getWorld().createExplosion(player.getLocation(), 0F, false);

        // Shoot the player in the sky
        player.setVelocity(player.getVelocity().clone().add(new Vector(0, 20, 0)));

        final String kickReason = (reason == null ? "FUCKOFF, and get your shit together!" : reason);

        // Log doom
        plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.DOOM, reason));

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // strike lightning
                player.getWorld().strikeLightningEffect(player.getLocation());

                // kill (if not done already)
                player.setHealth(0.0);
            }
        }.runTaskLater(plugin, 2L * 20L);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // message
                FUtil.staffAction(sender.getName(), "Banning " + player.getName(), true);
                msg(sender, player.getName() + " has been banned and IP is: " + ip);

                // generate explosion
                player.getWorld().createExplosion(player.getLocation(), 0F, false);;

                // kick player
                player.kickPlayer(ChatColor.RED + kickReason);
            }
        }.runTaskLater(plugin, 3L * 20L);

        return true;
    }
}
