package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Rank implements Displayable
{

    IMPOSTOR("an", "Impostor", Type.PLAYER, "Imp", ChatColor.YELLOW),
    NON_OP("a", "Non-Op", Type.PLAYER, "", ChatColor.GREEN),
    OP("an", "Op", Type.PLAYER, "OP", ChatColor.RED),
    IGNITION("is", "part of team Ignition!", Type.PLAYER, "Ignition", ChatColor.GOLD),
    SUPER_ADMIN("a", "Super Admin", Type.ADMIN, "SA", ChatColor.AQUA),
    TELNET_ADMIN("a", "Telnet Admin", Type.ADMIN, "STA", ChatColor.DARK_GREEN),
    SENIOR_ADMIN("a", "Senior Admin", Type.ADMIN, "SrA", ChatColor.GOLD),
    TELNET_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    SENIOR_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    EXECUTIVE("a", "Executive", Type.ADMIN, "Exec", ChatColor.RED),
    CO_FOUNDER("a", "Co Owner", Type.ADMIN, "Co Owner", ChatColor.BLUE),
    SYS_ADMIN("a", "System Admin", Type.ADMIN, "Sys Admin", ChatColor.DARK_RED), 
    OWNER_FOUNDER("the", "Owner and Founder", Type.ADMIN, "Owner", ChatColor.BLUE),
    CO_LEAD_DEV("the", "Co Lead Developer", Type.ADMIN, "Co Lead Dev", ChatColor.DARK_PURPLE),
    LEAD_DEV("the", "Lead Developer", Type.ADMIN, "Lead Dev", ChatColor.DARK_PURPLE),
    CO_FOUNDER_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    SYS_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    LEAD_DEV_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    CO_LEAD_DEV_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    OWNER_FOUNDER_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    SPECIAL_EXEC("a", "Special Executive", Type.ADMIN, "Special Exec", ChatColor.GOLD),
    EXEC_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    SPECIAL_EXEC_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    ASSISTANT_LEAD_DEV("the", "Assistant Lead Developer", Type.ADMIN, "Assistant Lead Dev", ChatColor.DARK_PURPLE),
    ASSISTANT_DEV_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    RETIRED_OWNER("the", "Retired Owner", Type.ADMIN, "Retired Owner", ChatColor.DARK_GRAY),
    RETIRED_OWNER_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE);
    
    
    @Getter
    private final Type type;
    @Getter
    private final String name;
    private final String determiner;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;

    private Rank(String determiner, String name, Type type, String abbr, ChatColor color)
    {
        this.type = type;
        this.name = name;
        this.determiner = determiner;
        this.tag = abbr.isEmpty() ? "" : "[" + abbr + "]";
        this.coloredTag = abbr.isEmpty() ? "" : ChatColor.DARK_GRAY + "[" + color + abbr + ChatColor.DARK_GRAY + "]" + color;
        this.color = color;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + color + ChatColor.ITALIC + name;
    }

    public boolean isConsole()
    {
        return getType() == Type.ADMIN_CONSOLE;
    }

    public int getLevel()
    {
        return ordinal();
    }

    public boolean isAtLeast(Rank rank)
    {
        if (getLevel() < rank.getLevel())
        {
            return false;
        }

        if (!hasConsoleVariant() || !rank.hasConsoleVariant())
        {
            return true;
        }

        return getConsoleVariant().getLevel() >= rank.getConsoleVariant().getLevel();
    }

    public boolean isAdmin()
    {
        return getType() == Type.ADMIN || getType() == Type.ADMIN_CONSOLE;
    }

    public boolean hasConsoleVariant()
    {
        return getConsoleVariant() != null;
    }

    public Rank getConsoleVariant()
    {
        switch (this)
        {
            case TELNET_ADMIN:
            case TELNET_CONSOLE:
                return TELNET_CONSOLE;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_CONSOLE;
            case CO_FOUNDER:
            case CO_FOUNDER_CONSOLE:
                return CO_FOUNDER_CONSOLE;
            case SYS_ADMIN:
            case SYS_CONSOLE:
                return SYS_CONSOLE;
            case LEAD_DEV:
            case LEAD_DEV_CONSOLE:
                return LEAD_DEV_CONSOLE;
            case OWNER_FOUNDER:
            case OWNER_FOUNDER_CONSOLE:
                return OWNER_FOUNDER_CONSOLE;
            case CO_LEAD_DEV:
            case CO_LEAD_DEV_CONSOLE:
                return CO_LEAD_DEV_CONSOLE;
            case EXECUTIVE:
            case EXEC_CONSOLE:
                return EXEC_CONSOLE;
            case SPECIAL_EXEC:
            case SPECIAL_EXEC_CONSOLE:
                return SPECIAL_EXEC_CONSOLE;
            case ASSISTANT_LEAD_DEV:
            case ASSISTANT_DEV_CONSOLE:
                return ASSISTANT_DEV_CONSOLE;
            case RETIRED_OWNER:
            case RETIRED_OWNER_CONSOLE:
                return RETIRED_OWNER_CONSOLE;
             
               
                
                 
               
                    
            default:
                return null;
        }
    }

    public Rank getPlayerVariant()
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

    public static Rank findRank(String string)
    {
        try
        {
            return Rank.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return Rank.NON_OP;
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
