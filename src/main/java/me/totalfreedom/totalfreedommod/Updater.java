package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.IOException;
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
            FileOutputStream output = new FileOutputStream("plugins/TotalFreedomMod.jar");
            FLog.info("Downloading latest version...");
            output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
            input.close();
            output.close();
            FLog.info("The latest version has been installed! Restarting server...");

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        for (Player player : server.getOnlinePlayers())
        {
            player.kickPlayer("The server has restarted as TotalFreedomMod was just updated.");
        }

        if (!plugin.amp.enabled)
        {
            server.shutdown();
        }
        else
        {
            plugin.amp.restartServer();
        }
    }

}
