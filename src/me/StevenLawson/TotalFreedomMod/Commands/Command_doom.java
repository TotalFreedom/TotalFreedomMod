package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.ADMIN_LEVEL;
import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.SOURCE_TYPE_ALLOWED;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SENIOR, source = SOURCE_TYPE_ALLOWED.ONLY_CONSOLE, ignore_permissions = false)
public class Command_doom extends TFM_Command
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

        TFM_Util.adminAction(sender.getName(), "Casting oblivion over " + p.getName(), true);
        TFM_Util.bcastMsg(p.getName() + " will be completely obliviated!", ChatColor.RED);

        final String IP = p.getAddress().getAddress().getHostAddress().trim();

        // remove from superadmin
        if (TFM_SuperadminList.isUserSuperadmin(p))
        {
            TFM_Util.adminAction(sender.getName(), "Removing " + p.getName() + " from the superadmin list.", true);
            TFM_SuperadminList.removeSuperadmin(p);
        }

        // remove from whitelist
        p.setWhitelisted(false);

        // deop
        p.setOp(false);

        // ban IP
        TFM_ServerInterface.banIP(IP, null, null, null);

        // ban name
        TFM_ServerInterface.banUsername(p.getName(), null, null, null);

        // set gamemode to survival
        p.setGameMode(GameMode.SURVIVAL);

        // clear inventory
        p.closeInventory();
        p.getInventory().clear();

        // ignite player
        p.setFireTicks(10000);

        // generate explosion
        p.getWorld().createExplosion(p.getLocation(), 4F);

        server.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                // strike lightning
                p.getWorld().strikeLightning(p.getLocation());

                // kill (if not done already)
                p.setHealth(0);
            }
        }, 40L); // 2 seconds

        server.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                // message
                TFM_Util.adminAction(sender.getName(), "Banning " + p.getName() + ", IP: " + IP, true);

                // generate explosion
                p.getWorld().createExplosion(p.getLocation(), 4F);

                // kick player
                p.kickPlayer(ChatColor.RED + "FUCKOFF, and get your shit together!");
            }
        }, 60L); // 3 seconds

        return true;
    }
}
