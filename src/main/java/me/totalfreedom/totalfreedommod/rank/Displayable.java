package me.totalfreedom.totalfreedommod.rank;

import net.md_5.bungee.api.ChatColor;

public interface Displayable
{

    public String getName();

    public String getTag();

    public String getAbbr();

    public ChatColor getColor();

    public org.bukkit.ChatColor getTeamColor();

    public String getColoredName();

    public String getColoredTag();

    public String getColoredLoginMessage();


    public boolean hasTeam();

}
