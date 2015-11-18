package me.totalfreedom.totalfreedommod.rank;

import org.bukkit.ChatColor;

public interface Rank
{

    public String getName();

    public String getTag();

    public ChatColor getColor();

    public String getColorString();

    public String getColoredName();

    public String getColoredTag();

    public String getColoredLoginMessage();

    public boolean isAtLeast(Rank rank);

    public int getLevel();

}
