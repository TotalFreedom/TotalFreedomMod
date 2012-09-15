package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TFM_RunSystemCommand implements Runnable
{
    private final String command;
    private final TotalFreedomMod plugin;

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
                        TFM_Log.info(line);
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
            TFM_Log.severe(ex.getMessage());
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex.getMessage());
        }
        catch (Throwable ex)
        {
            TFM_Log.severe(ex);
        }
    }
}
