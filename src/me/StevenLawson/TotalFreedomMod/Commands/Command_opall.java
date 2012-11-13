package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_opall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TFM_SuperadminList.isUserSuperadmin(sender) || senderIsConsole)
        {
            TFM_Util.adminAction(sender.getName(), "Opping all players on the server", false);

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

            for (Player p : server.getOnlinePlayers())
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
}
