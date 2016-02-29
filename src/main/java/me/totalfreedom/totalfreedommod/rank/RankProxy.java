package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public abstract class RankProxy implements Rank
{

    @Getter
    protected final Rank proxy;

    public RankProxy(Rank rank)
    {
        this.proxy = rank;
    }

    @Override
    public String getName()
    {
        return proxy.getName();
    }

    @Override
    public String getTag()
    {
        return proxy.getTag();
    }

    @Override
    public ChatColor getColor()
    {
        return proxy.getColor();
    }

    @Override
    public String getColoredName()
    {
        return proxy.getColoredName();
    }

    @Override
    public String getColoredTag()
    {
        return proxy.getColoredTag();
    }

    @Override
    public String getColoredLoginMessage()
    {
        return proxy.getColoredLoginMessage();
    }

    @Override
    public boolean isAtLeast(Rank rank)
    {
        return proxy.isAtLeast(rank);
    }

    @Override
    public int getLevel()
    {
        return proxy.getLevel();
    }

}
