package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

/*
    This class exists primarily due to the fact that it takes too long to get new builds onto the official
    TotalFreedom server. If you wish to delete this class you may do so.
*/

public class Updater extends FreedomService
{

    private final String UPDATE_SERVER_URL = "https://tfm.zeroepoch1969.rip";
    private final TotalFreedomMod.BuildProperties build = TotalFreedomMod.build;
    public boolean updateAvailable = false;

    public Updater(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        if (build.number != null)
        {
            checkForUpdates();
        }
    }

    @Override
    protected void onStop()
    {
    }

    private void checkForUpdates()
    {
        try
        {
            URL url = new URL(UPDATE_SERVER_URL + "/build");
            String webBuild = new Scanner(url.openStream()).useDelimiter("\\Z").next();
            if (!build.number.equals(webBuild))
            {
                updateAvailable = true;
            }
            FLog.info((updateAvailable ? "A new update is a available!" : "TFM is up-to-date!"));

        }
        catch (IOException ex)
        {
            FLog.warning("Failed to connect to the update server.");
        }
    }


    public void update()
    {
        try
        {
            URL url = new URL(UPDATE_SERVER_URL + "/TotalFreedomMod.jar");
            ReadableByteChannel input = Channels.newChannel(url.openStream());
            FileOutputStream output = new FileOutputStream(getFilePath());
            FLog.info("Downloading latest version...");
            output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
            input.close();
            output.close();
            FLog.info("The latest version has been installed! Restart the server for changes to take effect.");

        }
        catch (IOException ex)
        {
            FLog.severe(ex);
        }
    }
    public String getFilePath()
    {
            try
            {
                Method method = JavaPlugin.class.getDeclaredMethod("getFile");
                boolean wasAccessible = method.isAccessible();
                method.setAccessible(true);
                File file = (File) method.invoke(plugin);
                method.setAccessible(wasAccessible);

                return file.getPath();
            }
            catch (Exception e)
            {
                return "plugins" + File.separator + plugin.getName();
            }
    }
}
