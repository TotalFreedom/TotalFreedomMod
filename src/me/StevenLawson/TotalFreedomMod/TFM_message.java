package me.StevenLawson.TotalFreedomMod;

import org.bukkit.ChatColor;

// Work in progress
@Deprecated
public enum TFM_message {
	NO_PERMS(ChatColor.YELLOW + "You do not have permission to use this command."),
	YOU_ARE_OP(ChatColor.YELLOW + "You are now op!"),
	YOU_ARE_NOT_OP(ChatColor.YELLOW + "You are no longer op!"),
	CAKE_LYRICS("But there's no sense crying over every mistake. You just keep on trying till you run out of cake."),
	NOT_FROM_CONSOLE("This command may not be used from the console.")
	;
    private final String message;
    
    TFM_message(String message)
    {
    	this.message = message;
    }
    
    @Override
	public String toString()
    {
    	return message;
    }
}
