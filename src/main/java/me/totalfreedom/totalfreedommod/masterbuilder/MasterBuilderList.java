package me.totalfreedom.totalfreedommod.masterbuilder;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

public class MasterBuilderList extends FreedomService
{

    public static final String CONFIG_FILENAME = "masterbuilders.yml";

    @Getter
    private final Map<String, MasterBuilder> masterBuilders = Maps.newHashMap();
    private final Map<String, MasterBuilder> nameTable = Maps.newHashMap();
    private final Map<String, MasterBuilder> ipTable = Maps.newHashMap();
    //
    private final YamlConfig config;

    public MasterBuilderList(TotalFreedomMod plugin)
    {
        super(plugin);

        this.config = new YamlConfig(plugin, CONFIG_FILENAME, true);
    }

    @Override
    protected void onStart()
    {
        load();

        server.getServicesManager().register(Function.class, new Function<Player, Boolean>()
        {
            @Override
            public Boolean apply(Player player)
            {
                return isMasterBuilder(player);
            }
        }, plugin, ServicePriority.Normal);
    }

    @Override
    protected void onStop()
    {
        save();
    }

    public void load()
    {
        config.load();

        masterBuilders.clear();
        for (String key : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null)
            {
                logger.warning("Invalid master builder list format: " + key);
                continue;
            }

            MasterBuilder masterBuilder = new MasterBuilder(key);
            masterBuilder.loadFrom(section);

            if (!masterBuilder.isValid())
            {
                FLog.warning("Could not load master builder: " + key + ". Missing details!");
                continue;
            }

            masterBuilders.put(key, masterBuilder);
        }

        updateTables();
        FLog.info("Loaded " + masterBuilders.size() + " master builders with " + ipTable.size() + " IPs)");
    }

    public void save()
    {
        // Clear the config
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        for (MasterBuilder masterBuilder : masterBuilders.values())
        {
            masterBuilder.saveTo(config.createSection(masterBuilder.getConfigKey()));
        }

        config.save();
    }

    public synchronized boolean isMasterbuilderSync(CommandSender sender)
    {
        return isMasterBuilder(sender);
    }

    public boolean isMasterBuilder(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        MasterBuilder masterBuilder = getMasterBuilder((Player)sender);

        return masterBuilder != null;
    }

    public Map<String, MasterBuilder> getAllMasterBuilders()
    {
        return this.masterBuilders;
    }

    public MasterBuilder getMasterBuilder(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getMasterBuilder((Player)sender);
        }

        return getEntryByName(sender.getName());
    }

    public MasterBuilder getMasterBuilder(Player player)
    {
        String ip = Ips.getIp(player);
        MasterBuilder masterBuilder = getEntryByName(player.getName());

        // By name
        if (masterBuilder != null)
        {
            // Check if we're in online mode or if we have a matching IP
            if (server.getOnlineMode() || masterBuilder.getIps().contains(ip))
            {
                if (!masterBuilder.getIps().contains(ip))
                {
                    // Add the new IP if needed
                    masterBuilder.addIp(ip);
                    save();
                    updateTables();
                }
                return masterBuilder;
            }
        }

        // By ip
        masterBuilder = getEntryByIp(ip);
        if (masterBuilder != null)
        {
            // Set the new username
            masterBuilder.setName(player.getName());
            save();
            updateTables();
        }

        return null;
    }

    public MasterBuilder getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public MasterBuilder getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public MasterBuilder getEntryByIpFuzzy(String needleIp)
    {
        final MasterBuilder directMasterBuilder = getEntryByIp(needleIp);
        if (directMasterBuilder != null)
        {
            return directMasterBuilder;
        }

        for (String ip : ipTable.keySet())
        {
            if (FUtil.fuzzyIpMatch(needleIp, ip, 3))
            {
                return ipTable.get(ip);
            }
        }

        return null;
    }

    public void updateLastLogin(Player player)
    {
        final MasterBuilder masterBuilder = getMasterBuilder(player);
        if (masterBuilder == null)
        {
            return;
        }

        masterBuilder.setLastLogin(new Date());
        masterBuilder.setName(player.getName());
        save();
    }

    public boolean isMasterBuilderImpostor(Player player)
    {
        return getEntryByName(player.getName()) != null && !isMasterBuilder(player);
    }

    public boolean isIdentityMatched(Player player)
    {
        if (server.getOnlineMode())
        {
            return true;
        }

        MasterBuilder masterBuilder = getMasterBuilder(player);
        return masterBuilder != null && masterBuilder.getName().equalsIgnoreCase(player.getName());
    }

    public boolean addMasterBuilder(MasterBuilder masterBuilder)
    {
        if (!masterBuilder.isValid())
        {
            logger.warning("Could not add master builder: " + masterBuilder.getConfigKey() + " master builder is missing details!");
            return false;
        }

        final String key = masterBuilder.getConfigKey();

        // Store master builder, update views
        masterBuilders.put(key, masterBuilder);
        updateTables();

        // Save master builder
        masterBuilder.saveTo(config.createSection(key));
        config.save();

        return true;
    }

    public boolean removeMasterBuilder(MasterBuilder masterBuilder)
    {
        // Remove master builder, update views
        if (masterBuilders.remove(masterBuilder.getConfigKey()) == null)
        {
            return false;
        }
        updateTables();

        // 'Unsave' master builder
        config.set(masterBuilder.getConfigKey(), null);
        config.save();

        return true;
    }

    public void updateTables()
    {
        nameTable.clear();
        ipTable.clear();

        for (MasterBuilder masterBuilder : masterBuilders.values())
        {
            nameTable.put(masterBuilder.getName().toLowerCase(), masterBuilder);

            for (String ip : masterBuilder.getIps())
            {
                ipTable.put(ip, masterBuilder);
            }

        }
    }

    public Set<String> getMasterBuilderNames()
    {
        return nameTable.keySet();
    }

    public Set<String> getMasterBuilderIps()
    {
        return ipTable.keySet();
    }

}
