package me.StevenLawson.TotalFreedomMod;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class TotalFreedomMod extends JavaPlugin
{
@Override
public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
{
       if(commandLabel.equalsIgnoreCase("tfm")){
           sender.sendMessage("this is the best plugin ever");
       } 
}
}
