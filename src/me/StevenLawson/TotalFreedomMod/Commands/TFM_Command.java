package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_Command
{
    protected TotalFreedomMod plugin;
    protected Server server;
    
    public TFM_Command()
    {
    }
    
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Log.severe("Command Error: Command not implemented: " + cmd.getName());
        sender.sendMessage(ChatColor.RED + "Command Error: Command not implemented: " + cmd.getName());
        return false;
    }
    
    public void setPlugin(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }
    
    public Player getPlayer(String partialname) throws CantFindPlayerException
    {
        List<Player> matches = server.matchPlayer(partialname);
        if (matches.isEmpty())
        {
            for(Player p : server.getOnlinePlayers())
            {
                if(p.getDisplayName().toLowerCase().indexOf(partialname) != -1)
                {
                    return p;
                }
            }
            throw new CantFindPlayerException(partialname);
        }
        else
        {
            return matches.get(0);
        }
    }
}
