package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Title implements Displayable
{

    MASTER_BUILDER("a", "Master Builder", ChatColor.DARK_AQUA, "MB", false),
    VERIFIED_ADMIN("a", "Verified Admin", ChatColor.LIGHT_PURPLE, "VA", false),
    ASSISTANT_EXECUTIVE("an", "Assistant Executive", ChatColor.RED, "Asst Exec", false),
    EXECUTIVE("an", "Executive", ChatColor.RED, "Exec", true),
    DEVELOPER("a", "Developer", ChatColor.DARK_PURPLE, "Dev", true),
    OWNER("the", "Owner", ChatColor.DARK_RED, "Owner", true);

    private final String determiner;
    @Getter
    private final String name;
    @Getter
    private final String abbr;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;
    @Getter
    private final boolean hasTeam;

    private Title(String determiner, String name, ChatColor color, String tag, Boolean hasTeam)
    {
        this.determiner = determiner;
        this.name = name;
        this.coloredTag = ChatColor.DARK_GRAY + "[" + color + tag + ChatColor.DARK_GRAY + "]" + color;
        this.abbr = tag;
        this.tag = "[" + tag + "]";
        this.color = color;
        this.hasTeam = hasTeam;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }

    @Override
    public boolean hasTeam()
    {
        return hasTeam;
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + color + ChatColor.ITALIC + name;
    }

}
