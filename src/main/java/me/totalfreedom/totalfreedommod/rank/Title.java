package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Title implements RankBase
{

    DEVELOPER("a", "Dev", ChatColor.DARK_PURPLE),
    OWNER("the", "Owner", ChatColor.BLUE);

    @Getter
    private final String name;
    private final String determiner;
    @Getter
    private final String tag;
    @Getter
    private final ChatColor color;
    @Getter
    private final String colorString;

    private Title(String determiner, String tag, ChatColor... colors)
    {
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
        return getColorString() + getTag();
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + getColoredName();
    }

    @Override
    public int getLevel()
    {
        return ordinal();
    }

    @Override
    public boolean isAtLeast(RankBase rank)
    {
        return getLevel() >= rank.getLevel();
    }

}
