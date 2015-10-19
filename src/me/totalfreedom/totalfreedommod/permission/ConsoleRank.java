package me.totalfreedom.totalfreedommod.permission;

import org.bukkit.ChatColor;

public enum ConsoleRank implements Rank
{

    TELNET_CONSOLE(PlayerRank.TELNET_ADMIN),
    SENIOR_CONSOLE(PlayerRank.SENIOR_ADMIN);
    //
    private final PlayerRank appliedRank;

    //
    private ConsoleRank(PlayerRank appliedRank)
    {
        this.appliedRank = appliedRank;
    }

    @Override
    public String getName()
    {
        return "Console";
    }

    @Override
    public ChatColor getColor()
    {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public String getColorString()
    {
        return ChatColor.DARK_PURPLE.toString();
    }

    @Override
    public String getColoredName()
    {
        return getColor() + getName();
    }

    @Override
    public String getTag()
    {
        return "[Console]";
    }

    @Override
    public String getColoredTag()
    {
        return getColorString() + getTag();
    }

    @Override
    public String getColoredLoginMessage()
    {
        return "the " + getColorString() + " Console";
    }

    @Override
    public int getLevel()
    {
        return ordinal();
    }

    @Override
    public boolean isAtLeast(Rank rank)
    {
        return getLevel() >= rank.getLevel();
    }

    public static boolean hasConsole(PlayerRank playerRank)
    {
        for (ConsoleRank consoleRank : values())
        {
            if (consoleRank.appliedRank == playerRank)
            {
                return true;
            }
        }
        return false;
    }

    public static ConsoleRank forRank(PlayerRank playerRank)
    {
        for (ConsoleRank consoleRank : values())
        {
            if (consoleRank.appliedRank == playerRank)
            {
                return consoleRank;
            }
        }
        return TELNET_CONSOLE;
    }

}
