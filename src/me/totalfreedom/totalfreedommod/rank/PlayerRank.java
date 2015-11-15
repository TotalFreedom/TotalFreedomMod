package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum PlayerRank implements Rank
{
    // Order is important here
    IMPOSTOR(false, "an", "Imp", ChatColor.YELLOW, ChatColor.UNDERLINE),
    NON_OP(false, "a", "", ChatColor.GREEN),
    OP(false, "an", "OP", ChatColor.RED),
    SUPER_ADMIN(true, "a", "SA", ChatColor.GOLD),
    TELNET_ADMIN(true, "a", "StA", ChatColor.DARK_GREEN),
    SENIOR_ADMIN(true, "a", "SrA", ChatColor.LIGHT_PURPLE);
    //
    @Getter
    private final String name;
    private final String determiner;
    @Getter
    private final String tag;
    @Getter
    private final ChatColor color;
    @Getter
    private final String colorString;
    @Getter
    private final boolean admin;

    private PlayerRank(boolean admin, String determiner, String tag, ChatColor... colors)
    {
        this.admin = admin;

        final String[] nameParts = name().toLowerCase().split("_");
        String tempName = "";
        for (String part : nameParts)
        {
            tempName = Character.toUpperCase(part.charAt(0)) + part.substring(1) + " ";
        }
        name = tempName.trim();

        this.determiner = determiner;
        this.tag = "[" + tag + "]";

        this.color = colors[0];
        String tColor = "";
        for (ChatColor lColor : colors)
        {
            tColor += lColor.toString();
        }
        colorString = tColor;
    }

    @Override
    public String getColoredName()
    {
        return getColorString() + getName();
    }

    @Override
    public String getColoredTag()
    {
        return getColor() + getTag();
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + getColoredName();
    }

    public boolean hasConsole()
    {
        return ConsoleRank.hasConsole(this);
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

    public static PlayerRank forString(String string)
    {
        try
        {
            return PlayerRank.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return PlayerRank.NON_OP;
    }
}
