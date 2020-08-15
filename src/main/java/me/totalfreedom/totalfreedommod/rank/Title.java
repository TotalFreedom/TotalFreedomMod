package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public enum Title implements Displayable
{

    DONATOR("a", "Premium Member", ChatColor.of("#ff5600"), org.bukkit.ChatColor.LIGHT_PURPLE, "Premium", true),
    MASTER_BUILDER("a", "Master Builder", ChatColor.DARK_AQUA, org.bukkit.ChatColor.DARK_AQUA, "MB", true),
    VERIFIED_STAFF("a", "Verified Staff", ChatColor.LIGHT_PURPLE, org.bukkit.ChatColor.LIGHT_PURPLE, "VS", false),
    ASSISTANT_EXECUTIVE("an", "Assistant Executive", ChatColor.RED, org.bukkit.ChatColor.RED, "Asst Exec", true),
    EXECUTIVE("an", "Executive", ChatColor.RED, org.bukkit.ChatColor.RED, "Exec", true),
    DEVELOPER("a", "Developer", ChatColor.DARK_PURPLE, org.bukkit.ChatColor.DARK_PURPLE, "Dev", true),
    OWNER("the", "Owner", ChatColor.of("#ff0000"), org.bukkit.ChatColor.DARK_RED, "Owner", true);

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
    private final org.bukkit.ChatColor teamColor;
    @Getter
    private final boolean hasTeam;

    Title(String determiner, String name, ChatColor color, org.bukkit.ChatColor teamColor, String tag, Boolean hasTeam)
    {
        this.determiner = determiner;
        this.name = name;
        this.coloredTag = ChatColor.DARK_GRAY + "[" + color + tag + ChatColor.DARK_GRAY + "]" + color;
        this.abbr = tag;
        this.tag = "[" + tag + "]";
        this.color = color;
        this.teamColor = teamColor;
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
        return determiner + " " + color + name;
    }
}