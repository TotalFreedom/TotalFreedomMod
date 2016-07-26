package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.unraveledmc.unraveledmcmod.admin.Admin;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("wildcard"))
        {
            msg("What the hell are you trying to do, you stupid idiot...", ChatColor.RED);
            return true;
        }
        if (args[0].equals("gtfo"))
        {
            msg("Nice try", ChatColor.RED);
            return true;
        }
        if (args[0].equals("doom"))
        {
            msg("Look, we all hate people, but this is not the way to deal with it, doom is evil enough!", ChatColor.RED);
            return true;
        }
        if (args[0].equals("saconfig"))
        {
            msg("WOA, WTF are you trying to do???", ChatColor.RED);
            return true;
        }
        if (args[0].equals("gcmd") && args.length > 2)
        {
            if (args[2].equals("wildcard") || args[2].equals("gcmd") || args[2].equals("executive") || args[2].equals("exec") || args[2].equals("stop"))
            {
                rouge(playerSender, senderIsConsole);
                return true;
            }
        }

        String baseCommand = StringUtils.join(args, " ");

        if (plugin.cb.isCommandBlocked(baseCommand, sender))
        {
            // CommandBlocker handles messages and broadcasts
            return true;
        }

        for (Player player : server.getOnlinePlayers())
        {
            String out_command = baseCommand.replaceAll("\\x3f", player.getName());
            msg("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }
        return true;
    }
    public void rouge(Player p, boolean sic)
    {
        String argsList = StringUtils.join(args, " ");
        msg("No, hell no, that is rouge activity right there, this has been logged!", ChatColor.RED);
        if (!sic)
        {
            Admin admin = plugin.al.getAdmin(p);
            admin.setActive(false);
            plugin.al.save();
            plugin.al.updateTables();
            p.setOp(false);
            plugin.da.setAdminDeopped(p.getName(), true);
            FPlayer playerData = plugin.pl.getPlayer(p);
            Location targetPos = p.getLocation().clone().add(0, 1, 0);
            playerData.getCageData().cage(targetPos, Material.GLASS, Material.AIR);
            p.setGameMode(GameMode.SURVIVAL);
            p.closeInventory();
        }
        FUtil.adminAction(sender.getName(), "Has just attempted to execute the command /wildcard " + argsList + "!", true);
        FUtil.bcastMsg("This is rouge activity, " + (sic ? sender.getName() + " is console! Please standby and alert an executive admin or owner!" : p.getName() + " has been removed from the admin list, deopped, and caged!"), ChatColor.RED);
    }
}
