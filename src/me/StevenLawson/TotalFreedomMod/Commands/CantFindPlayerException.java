package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;

public class CantFindPlayerException extends Exception
{
    public CantFindPlayerException()
    {
        super(ChatColor.GRAY + "Can't find player.");
    }

    public CantFindPlayerException(String msg)
    {
        super(ChatColor.GRAY + "Can't find player: " + msg);
    }
}
