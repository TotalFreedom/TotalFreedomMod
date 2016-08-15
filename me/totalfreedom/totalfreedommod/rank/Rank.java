package me.totalfreedom.totalfreedommod.rank;

import org.bukkit.*;

public enum Rank implements Displayable
{
    IMPOSTOR("an", "Impostor", Type.PLAYER, "Imp", ChatColor.YELLOW), 
    NON_OP("a", "Non-Op", Type.PLAYER, "", ChatColor.GREEN), 
    OP("an", "Op", Type.PLAYER, "OP", ChatColor.RED), 
    SUPER_ADMIN("a", "Super Admin", Type.ADMIN, "SA", ChatColor.AQUA), 
    TELNET_ADMIN("a", "Telnet Admin", Type.ADMIN, "STA", ChatColor.DARK_GREEN), 
    SENIOR_ADMIN("a", "Senior Admin", Type.ADMIN, "SrA", ChatColor.GOLD), 
    TELNET_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_GREEN), 
    SENIOR_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.LIGHT_PURPLE);
    
    private final Type type;
    private final String name;
    private final String determiner;
    private final String tag;
    private final String coloredTag;
    private final ChatColor color;
    
    private Rank(final String determiner, final String name, final Type type, final String abbr, final ChatColor color) {
        this.type = type;
        this.name = name;
        this.determiner = determiner;
        this.tag = (abbr.isEmpty() ? "" : ("[" + abbr + "]"));
        this.coloredTag = (abbr.isEmpty() ? "" : (ChatColor.DARK_GRAY + "[" + color + abbr + ChatColor.DARK_GRAY + "]" + color));
        this.color = color;
    }
    
    @Override
    public String getColoredName() {
        return this.color + this.name;
    }
    
    @Override
    public String getColoredLoginMessage() {
        return this.determiner + " " + this.color + ChatColor.ITALIC + this.name;
    }
    
    public boolean isConsole() {
        return this.getType() == Type.ADMIN_CONSOLE;
    }
    
    public int getLevel() {
        return this.ordinal();
    }
    
    public boolean isAtLeast(final Rank rank) {
        return this.getLevel() >= rank.getLevel() && (!this.hasConsoleVariant() || !rank.hasConsoleVariant() || this.getConsoleVariant().getLevel() >= rank.getConsoleVariant().getLevel());
    }
    
    public boolean isAdmin() {
        return this.getType() == Type.ADMIN || this.getType() == Type.ADMIN_CONSOLE;
    }
    
    public boolean hasConsoleVariant() {
        return this.getConsoleVariant() != null;
    }
    
    public Rank getConsoleVariant() {
        switch (this) {
            case TELNET_ADMIN:
            case TELNET_CONSOLE: {
                return Rank.TELNET_CONSOLE;
            }
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE: {
                return Rank.SENIOR_CONSOLE;
            }
            default: {
                return null;
            }
        }
    }
    
    public Rank getPlayerVariant() {
        switch (this) {
            case TELNET_ADMIN:
            case TELNET_CONSOLE: {
                return Rank.TELNET_ADMIN;
            }
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE: {
                return Rank.SENIOR_ADMIN;
            }
            default: {
                return null;
            }
        }
    }
    
    public static Rank findRank(final String string) {
        try {
            return valueOf(string.toUpperCase());
        }
        catch (Exception ignored) {
            return Rank.NON_OP;
        }
    }
    
    public Type getType() {
        return this.type;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getTag() {
        return this.tag;
    }
    
    @Override
    public String getColoredTag() {
        return this.coloredTag;
    }
    
    @Override
    public ChatColor getColor() {
        return this.color;
    }
    
    public enum Type
    {
        PLAYER, 
        ADMIN, 
        ADMIN_CONSOLE;
        
        public boolean isAdmin() {
            return this != Type.PLAYER;
        }
    }
}
