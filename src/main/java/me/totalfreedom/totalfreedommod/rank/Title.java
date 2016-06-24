package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Title implements Displayable
{

    MASTER_BUILDER("a", "Master Builder", ChatColor.DARK_AQUA, "Master-Builder"),
    DEVELOPER("a", "TotalFreedom Developer", ChatColor.DARK_PURPLE, "TF-Dev"),
    UMCDEV("an", "UnraveledMC Developer", ChatColor.DARK_PURPLE, "UMC-Dev"),
    EXEC("an", "Executive Admin", ChatColor.DARK_RED, "Exec"),
    OWNER("an", "Owner", ChatColor.BLUE, "Owner");

    private final String determiner;
    @Getter
    private final String name;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;

    private Title(String determiner, String name, ChatColor color, String tag)
    {
        this.determiner = determiner;
        this.name = name;
        this.tag = "[" + tag + "]";
        this.coloredTag = ChatColor.DARK_GRAY + "[" + color + tag + ChatColor.DARK_GRAY + "]" + color;
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

}
