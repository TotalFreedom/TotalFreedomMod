package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Scare those nasty griefers!.", usage = "/<command> <playername>")
public class Command_griefer extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        final Player player;
        try
        {
            player = getPlayer(args[0]);
        }
        catch (PlayerNotFoundException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        TFM_Util.bcastMsg(player.getName() + " Is a Griefer!", ChatColor.RED);
        TFM_Util.bcastMsg(player.getName() + " will be completely obliviated!", ChatColor.RED);

        final String IP = player.getAddress().getAddress().getHostAddress().trim();

		// remove from whitelist
        player.setWhitelisted(false);

        // deop
        player.setOp(false);

        // ban IP
        TFM_ServerInterface.banIP(IP, null, null, null);

        // ban name
        TFM_ServerInterface.banUsername(player.getName(), null, null, null);

        // set gamemode to survival
        player.setGameMode(GameMode.SURVIVAL);

        // clear inventory
        player.closeInventory();
        player.getInventory().clear();

        // ignite player
        player.setFireTicks(10000);

        // generate explosion
        player.getWorld().createExplosion(player.getLocation(), 4F);

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
        }.runTaskLater(plugin, 20L * 2L);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // message
                TFM_Util.adminAction(sender.getName(), "Banning " + player.getName() + ", IP: " + IP, true);
                TFM_Util.bcastMsg(player.getName() + " Is now banned!", ChatColor.RED);
                // generate explosion
                player.getWorld().createExplosion(player.getLocation(), 4F);

                // kick player
                player.kickPlayer(ChatColor.RED + "GTFO, WE DONT NEED PLAYERS LIKE YOU ON OUR SERVER!");
            }
        }.runTaskLater(plugin, 20L * 3L);

        return true;
    }
