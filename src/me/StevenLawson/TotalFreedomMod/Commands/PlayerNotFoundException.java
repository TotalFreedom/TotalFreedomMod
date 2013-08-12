package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;

public class PlayerNotFoundException extends Exception
{
    // apparently, java needs this
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException()
    {
        super(ChatColor.GRAY + "Can't find player.");
    }

    public PlayerNotFoundException(String msg)
    {
        super(ChatColor.GRAY + "Can't find player: " + msg);
    }
}
