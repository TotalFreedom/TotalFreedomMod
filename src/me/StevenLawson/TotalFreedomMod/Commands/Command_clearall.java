package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//This command was coded initially by JeromSar

public class Command_clearall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!(TFM_Util.isUserSuperadmin(sender) || senderIsConsole))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        CommandSender cSender;
        if (senderIsConsole)
        {
            cSender = server.getConsoleSender();
        }
        else
        {
            cSender = sender;
        }

        server.dispatchCommand(cSender, "rd"); // remove entities
        server.dispatchCommand(cSender, "denick"); // remove nicks
        server.dispatchCommand(cSender, "uall"); // undisguise all

        return true;
    }
}
