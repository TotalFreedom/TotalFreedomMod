package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_halt extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("all"))
        {
            TFM_Util.adminAction(sender.getName(), "Halting all non-superadmins.", true);
            int counter = 0;
            for (Player p : server.getOnlinePlayers())
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    setHalted(p, true);
                    counter++;
                }
            }
            playerMsg("Halted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unhalting all players.", true);
            int counter = 0;
            for (Player p : server.getOnlinePlayers())
            {
                if (TFM_UserInfo.getPlayerData(p).isHalted())
                {
                    setHalted(p, false);
                    counter++;
                }
            }
            playerMsg("Unhalted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_UserInfo info;
            int count = 0;
            for (Player hp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(hp);
                if (info.isHalted())
                {
                    if (count == 0)
                    {
                        playerMsg(sender, "Halted players:");
                    }
                    playerMsg("- " + hp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg("There are currently no halted players.");
            }
            return true;
        }

        Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;

        }

        if (!TFM_UserInfo.getPlayerData(p).isHalted())
        {
            TFM_Util.adminAction(sender.getName(), "Halting " + p.getName(), true);
            setHalted(p, true);
            return true;
        }
        else
        {
            TFM_Util.adminAction(sender.getName(), "Unhalting " + p.getName(), true);
            setHalted(p, false);
            return true;
        }
    }

    private static void setHalted(Player p, boolean is_halted)
    {
        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);

        if (is_halted)
        {
            p.setOp(false);
            p.setGameMode(GameMode.SURVIVAL);
            p.setFlying(false);
            p.setDisplayName(p.getName());
            p.closeInventory();
            p.setTotalExperience(0);

            playerdata.stopOrbiting();
            playerdata.setFrozen(true);
            playerdata.setMuted(true);
            playerdata.setHalted(true);

            p.sendMessage(ChatColor.GRAY + "You have been halted, don't move!");
        }
        else
        {
            p.setOp(true);
            p.setGameMode(GameMode.CREATIVE);
            playerdata.setFrozen(false);
            playerdata.setMuted(false);
            playerdata.setHalted(false);
            p.sendMessage(ChatColor.GRAY + "You are no longer halted.");
        }
    }
}
