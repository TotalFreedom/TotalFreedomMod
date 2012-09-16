package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;

public class CantFindPlayerException extends Exception
{
	// apparently, java needs this
	private static final long serialVersionUID = 1L;

	public CantFindPlayerException()
    {
        super(ChatColor.GRAY + "Can't find player.");
    }

    public CantFindPlayerException(String msg)
    {
        super(ChatColor.GRAY + "Can't find player: " + msg);
    }
}
