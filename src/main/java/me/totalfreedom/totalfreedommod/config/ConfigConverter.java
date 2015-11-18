package me.totalfreedom.totalfreedommod.config;

import java.io.File;
import java.util.UUID;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;

public class ConfigConverter
{

    public static void convert()
    {

        for (File file : new File(TotalFreedomMod.plugin.getDataFolder(), "players").listFiles())
        {
            if (file.isDirectory())
            {
                continue;
            }

            final UUID uuid;
            try
            {
                uuid = UUID.fromString(file.getName().split(".")[0]);
            }
            catch (IllegalArgumentException ex)
            {
                continue;
            }

        }

    }

}
