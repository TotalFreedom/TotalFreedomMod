package me.StevenLawson.TotalFreedomMod;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TFM_EssentialsBridge
{
    private Essentials essentialsPlugin = null;

    private TFM_EssentialsBridge()
    {
    }

    public Essentials getEssentialsPlugin()
    {
        if (this.essentialsPlugin == null)
        {
            try
            {
                final Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentials != null)
                {
                    if (essentials instanceof Essentials)
                    {
                        this.essentialsPlugin = (Essentials) essentials;
                    }
                }
            }
            catch (Exception ex)
            {
                TFM_Log.severe(ex);
            }
        }
        return this.essentialsPlugin;
    }

    public User getEssentialsUser(String username)
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.getUserMap().getUser(username);
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public void setNickname(String username, String nickname)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                user.setNickname(nickname);
                user.setDisplayNick();
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public long getLastActivity(String username)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                return TFM_Util.<Long>getField(user, "lastActivity"); // This is weird
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
        return 0L;
    }

    public boolean isEssentialsEnabled()
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.isEnabled();
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
        return false;
    }

    public static TFM_EssentialsBridge getInstance()
    {
        return TFM_EssentialsBridgeHolder.INSTANCE;
    }

    private static class TFM_EssentialsBridgeHolder
    {
        private static final TFM_EssentialsBridge INSTANCE = new TFM_EssentialsBridge();
    }
}
