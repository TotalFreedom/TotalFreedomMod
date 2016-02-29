package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum PlayerRank implements Rank
{

    IMPOSTOR(Type.PLAYER, "an", "Imp", ChatColor.YELLOW),
    NON_OP(Type.PLAYER, "a", "", ChatColor.GREEN),
    OP(Type.PLAYER, "an", "OP", ChatColor.RED),
    SUPER_ADMIN(Type.ADMIN, "a", "SA", ChatColor.GOLD),
    TELNET_ADMIN(Type.ADMIN, "a", "StA", ChatColor.DARK_GREEN),
    SENIOR_ADMIN(Type.ADMIN, "a", "SrA", ChatColor.LIGHT_PURPLE),
    TELNET_CONSOLE(),
    SENIOR_CONSOLE();
    //
    @Getter
    private final Type type;
    @Getter
    private final String name;
    private final String determiner;
    @Getter
    private final String tag;
    @Getter
    private final ChatColor color;

    private PlayerRank()
    {
        this("Console", Type.ADMIN_CONSOLE, "the", "Console", ChatColor.DARK_PURPLE);
    }

    private PlayerRank(Type type, String determiner, String tag, ChatColor color)
    {
        this.type = type;

        // Name
        final String[] nameParts = name().toLowerCase().split("_");
        String tempName = "";
        for (String part : nameParts)
        {
            tempName = Character.toUpperCase(part.charAt(0)) + part.substring(1) + " ";
        }
        name = tempName.trim();

        this.determiner = determiner;
        this.tag = "[" + tag + "]";

        // Colors
        this.color = color;
    }

    private PlayerRank(String name, Type type, String determiner, String tag, ChatColor color)
    {
        this.type = type;
        this.name = name;
        this.determiner = determiner;
        this.tag = "[" + tag + "]";
        this.color = color;
    }

    @Override
    public String getColoredName()
    {
        return getColor() + getName();
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

    public boolean isConsole()
    {
        return getType() == Type.ADMIN_CONSOLE;
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

    public boolean isAdmin()
    {
        return getType() == Type.ADMIN || getType() == Type.ADMIN_CONSOLE;
    }

    public boolean hasConsole()
    {
        return getConsoleVariant() != null;
    }

    public PlayerRank getConsoleVariant()
    {
        switch (this)
        {
            case TELNET_ADMIN:
            case TELNET_CONSOLE:
                return TELNET_CONSOLE;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_CONSOLE;
            default:
                return null;
        }
    }

    public PlayerRank getPlayerVariant()
    {
        switch (this)
        {
            case TELNET_ADMIN:
            case TELNET_CONSOLE:
                return TELNET_ADMIN;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_ADMIN;
            default:
                return null;
        }
    }

    public static PlayerRank findRank(String string)
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

    public static enum Type
    {

        PLAYER,
        ADMIN,
        ADMIN_CONSOLE;

        public boolean isAdmin()
        {
            return this != PLAYER;
        }
    }
}
