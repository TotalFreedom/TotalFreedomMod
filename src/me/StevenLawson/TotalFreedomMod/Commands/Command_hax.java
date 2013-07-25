package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "To hack the server. Why did I write this? I have no clue.", usage = "/<command>")
public class Command_hax extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, final Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TFM_SuperadminList.isUserSuperadmin(sender))
        {
            playerMsg("What the hell are you trying to do, get yourself banned?");
            return true;
        }
        
        //GO!
        TFM_Util.bcastMsg(sender.getName() + " is trying to hack the server!", ChatColor.RED);
        
        //And here we are.
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                TFM_ServerInterface.banUsername(sender_p.getName(), ChatColor.RED + "You have been temporarily banned for 10 minutes",
                sender.getName(), TFM_Util.parseDateOffset("10m"));
        sender_p.kickPlayer(ChatColor.RED + "You have been temporarily banned for using harmful commands.");
        
    TFM_Util.bcastMsg(sender.getName() + " was kicked for trying to use harmful commands.", ChatColor.RED);
            }
        }, 20L); //1 Second
        return true;
    }
}
