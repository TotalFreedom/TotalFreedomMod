package me.totalfreedom.totalfreedommod.punishments;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.pravian.aero.config.YamlConfig;

public class PunishmentList extends FreedomService
{

    private final Set<Punishment> punishments = Sets.newHashSet();
    public static final String CONFIG_FILENAME = "punishments.yml";

    //
    private final YamlConfig config;

    public PunishmentList(TotalFreedomMod plugin)
    {
        super(plugin);
        this.config = new YamlConfig(plugin, CONFIG_FILENAME);
    }

    @Override
    protected void onStart()
    {
        config.load();

        punishments.clear();
        for (String id : config.getKeys(false))
        {
            if (!config.isConfigurationSection(id))
            {
                FLog.warning("Failed to load punishment number " + id + "!");
                continue;
            }

            Punishment punishment = new Punishment();
            punishment.loadFrom(config.getConfigurationSection(id));

            if (!punishment.isValid())
            {
                FLog.warning("Not adding punishment number " + id + ". Missing information.");
                continue;
            }

            punishments.add(punishment);
        }

        FLog.info("Loaded " + punishments.size() + " punishments.");
    }

    @Override
    protected void onStop()
    {
        saveAll();
        logger.info("Saved " + punishments.size() + " player bans");
    }

    public void saveAll()
    {
        config.clear();

        for (Punishment punishment : punishments)
        {
            punishment.saveTo(config.createSection(String.valueOf(punishment.hashCode())));
        }

        // Save config
        config.save();
    }

    public int clear()
    {
        int removed = punishments.size();
        punishments.clear();
        saveAll();

        return removed;
    }

    public int clear(String username)
    {
        List<Punishment> removed = new ArrayList<>();

        for (Punishment punishment : punishments)
        {
            if (punishment.getUsername().equalsIgnoreCase(username))
            {
                removed.add(punishment);
            }
        }

        if (removed.size() != 0)
        {
            punishments.removeAll(removed);
            saveAll();
        }

        return removed.size();
    }

    public int getLastPunishmentID()
    {
        int size = punishments.size();

        if (size == 0)
        {
            return 1;
        }

        return size;
    }

    public boolean logPunishment(Punishment punishment)
    {
        if (punishments.add(punishment))
        {
            saveAll();
            return true;
        }

        return false;
    }

}
