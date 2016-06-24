package me.totalfreedom.totalfreedommod;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.PropertyManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;

public class ServerInterface extends FreedomService
{

    public static final String COMPILE_NMS_VERSION = "v1_9_R1";

    public ServerInterface(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public static void warnVersion()
    {
        final String nms = FUtil.getNmsVersion();

        if (!COMPILE_NMS_VERSION.equals(nms))
        {
            FLog.warning(TotalFreedomMod.pluginName + " is compiled for " + COMPILE_NMS_VERSION + " but the server is running version " + nms + "!");
            FLog.warning("This might result in unexpected behaviour!");
        }
    }

    public void setOnlineMode(boolean mode)
    {
        final PropertyManager manager = getServer().getPropertyManager();
        manager.setProperty("online-mode", mode);
        manager.savePropertiesFile();
    }

    public int purgeWhitelist()
    {
        String[] whitelisted = getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.length;
        for (EntityPlayer player : getServer().getPlayerList().players)
        {
            getServer().getPlayerList().getWhitelist().remove(player.getProfile());
        }

        try
        {
            getServer().getPlayerList().getWhitelist().save();
        }
        catch (Exception ex)
        {
            FLog.warning("Could not purge the whitelist!");
            FLog.warning(ex);
        }
        return size;
    }

    public boolean isWhitelisted()
    {
        return getServer().getPlayerList().getHasWhitelist();
    }

    public List<?> getWhitelisted()
    {
        return Arrays.asList(getServer().getPlayerList().getWhitelisted());
    }

    public String getVersion()
    {
        return getServer().getVersion();
    }

    private MinecraftServer getServer()
    {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }

}
