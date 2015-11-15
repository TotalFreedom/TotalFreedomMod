package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import net.pravian.aero.component.service.AbstractService;

public class ServerInterface extends AbstractService<TotalFreedomMod>
{
    public static final String COMPILE_NMS_VERSION = "v1_8_R3";

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

    public void setOnlineMode(boolean mode)
    {
        final PropertyManager manager = MinecraftServer.getServer().getPropertyManager();
        manager.setProperty("online-mode", mode);
        manager.savePropertiesFile();
    }

    public int purgeWhitelist()
    {
        String[] whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.length;
        for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players)
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().remove(player.getProfile());
        }

        try
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().save();
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
        return MinecraftServer.getServer().getPlayerList().getHasWhitelist();
    }

    public List<?> getWhitelisted()
    {
        return Arrays.asList(MinecraftServer.getServer().getPlayerList().getWhitelisted());
    }

    public String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }

}
