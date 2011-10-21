package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TFM_RunSystemCommand implements Runnable
{
    private static final Logger log = Logger.getLogger("Minecraft");
    private String command;
    private TotalFreedomMod plugin;

    public TFM_RunSystemCommand(String command, TotalFreedomMod plugin)
    {
        this.command = command;
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        try
        {
            final ProcessBuilder childBuilder = new ProcessBuilder(command);
            childBuilder.redirectErrorStream(true);
            childBuilder.directory(plugin.getDataFolder().getParentFile().getParentFile());
            final Process child = childBuilder.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
            try
            {
                child.waitFor();
                String line;
                do
                {
                    line = reader.readLine();
                    if (line != null)
                    {
                        log.log(Level.INFO, line);
                    }
                }
                while (line != null);
            }
            finally
            {
                reader.close();
            }
        }
        catch (InterruptedException ex)
        {
            log.log(Level.SEVERE, ex.getMessage());
        }
        catch (IOException ex)
        {
            log.log(Level.SEVERE, ex.getMessage());
        }
        catch (Throwable ex)
        {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
