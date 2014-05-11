package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import org.bukkit.util.FileUtil;

public class TFM_PermbanList
{
    private static final List<String> PERMBANNED_PLAYERS;
    private static final List<String> PERMBANNED_IPS;

    static
    {
        PERMBANNED_PLAYERS = new ArrayList<String>();
        PERMBANNED_IPS = new ArrayList<String>();
    }

    private TFM_PermbanList()
    {
        throw new AssertionError();
    }

    public static List<String> getPermbannedPlayers()
    {
        return Collections.unmodifiableList(PERMBANNED_PLAYERS);
    }

    public static List<String> getPermbannedIps()
    {
        return Collections.unmodifiableList(PERMBANNED_IPS);
    }

    public static void load()
    {
        PERMBANNED_PLAYERS.clear();
        PERMBANNED_IPS.clear();

        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, TotalFreedomMod.PERMBAN_FILE, true);
        config.load();

        for (String playername : config.getKeys(false))
        {
            PERMBANNED_PLAYERS.add(playername.toLowerCase().trim());

            List<String> playerIps = config.getStringList(playername);
            for (String ip : playerIps)
            {
                ip = ip.trim();
                if (!PERMBANNED_IPS.contains(ip))
                {
                    PERMBANNED_IPS.add(ip);
                }
            }
        }

        TFM_Log.info("Loaded " + PERMBANNED_PLAYERS.size() + " permanently banned players and " + PERMBANNED_IPS.size() + " permanently banned IPs.");
    }
}
