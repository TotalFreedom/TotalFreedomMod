package me.unraveledmc.unraveledmcmod.command;

import java.util.Date;
import me.unraveledmc.unraveledmcmod.admin.Admin;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Self verification command for admin imposters", usage = "/<command> <password>")
public class Command_verify extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (ConfigEntry.VERIFY_ENABLED.getBoolean().equals(false))
        {
            msg("Self verification is currently disabled!", ChatColor.RED);
            return true;
        }
        if (plugin.al.isAdminImpostor(playerSender))
        {
            if (args.length > 0)
            {
                String inputedPassword = StringUtils.join(args, " ");
                if (inputedPassword.equals(ConfigEntry.VERIFY_PASSWORD.getString()))
                {
                    String name = playerSender != null ? playerSender.getName() : args[1];
                    Admin admin = null;
                    for (Admin loopAdmin : plugin.al.getAllAdmins().values())
                    {
                        if (loopAdmin.getName().equalsIgnoreCase(name))
                        {
                            admin = loopAdmin;
                            break;
                        }
                    }
                    FUtil.bcastMsg(playerSender.getName() + " has verified themself!", ChatColor.GOLD);
                    FUtil.adminAction(ConfigEntry.SERVER_NAME.getString(), "Readding " + admin.getName() + " to the admin list", true);
                    if (playerSender != null)
                    {
                        admin.setName(playerSender.getName());
                        admin.addIp(Ips.getIp(playerSender));
                    }
                    admin.setActive(true);
                    admin.setLastLogin(new Date());
                    plugin.al.save();
                    plugin.al.updateTables();
                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                }
                else
                {
                    msg("Incorrect password!", ChatColor.RED);
                    return true;
                }
                return true;
            }
        } 
        else
        {
            if (plugin.al.isAdmin(sender))
            {
                msg("You are not an imposter, therefore you do not need to verify!", ChatColor.RED);
                return true;
            }
            else
            {
                msg("You are not an admin!", ChatColor.RED);
                return true;
            }
        }
        return false;
    }
}
