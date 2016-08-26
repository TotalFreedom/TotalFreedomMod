package me.totalfreedom.totalfreedommod;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.banning.PermbanList;
import me.totalfreedom.totalfreedommod.rank.Rank;
import net.pravian.aero.component.PluginComponent;
import net.pravian.aero.config.YamlConfig;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigConverter extends PluginComponent<TotalFreedomMod>
{

    public static final int CURRENT_CONFIG_VERSION = 1;

    public ConfigConverter(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    public void convert()
    {
        File data = plugin.getDataFolder();
        data.mkdirs();
        File versionFile = new File(data, "version.yml");

        boolean convert = false;
        if (!versionFile.exists() && data.listFiles().length > 0)
        {
            convert = true;
        }

        YamlConfig config = new YamlConfig(plugin, versionFile, true);
        config.load();

        if (config.getInt("version", -1) < CURRENT_CONFIG_VERSION)
        {
            convert = true;
        }

        if (!convert)
        {
            return;
        }

        logger.warning("Converting old configs to new format...");

        File backup = new File(data, "backup_old_format");
        backup.mkdirs();

        for (File file : data.listFiles())
        {
            if (file.equals(backup) || file.equals(versionFile))
            {
                continue;
            }

            try
            {
                Files.move(file, new File(backup, file.getName()));
            }
            catch (IOException ex)
            {
                logger.severe("Could not backup file: " + file.getName());
                logger.severe(ex);
            }
        }

        convertSuperadmins(new File(backup, "superadmin.yml"));
        convertPermbans(new File(backup, "permban.yml"));

        logger.info("Conversion complete!");
    }

    private void convertSuperadmins(File oldFile)
    {
        if (!oldFile.exists() || !oldFile.isFile())
        {
            logger.warning("No old superadmin list found!");
            return;
        }

        // Convert old admin list
        YamlConfig oldYaml = new YamlConfig(plugin, oldFile, false);
        oldYaml.load();

        ConfigurationSection admins = oldYaml.getConfigurationSection("admins");
        if (admins == null)
        {
            logger.warning("No admin section in superadmin list!");
            return;
        }

        List<Admin> conversions = Lists.newArrayList();
        for (String uuid : admins.getKeys(false))
        {
            ConfigurationSection asec = admins.getConfigurationSection(uuid);
            if (asec == null)
            {
                logger.warning("Invalid superadmin format for admin: " + uuid);
                continue;
            }

            String username = asec.getString("last_login_name");
            Rank rank;
            if (asec.getBoolean("is_senior_admin"))
            {
                rank = Rank.SENIOR_ADMIN;
            }
            else if (asec.getBoolean("is_telnet_admin"))
            {
                rank = Rank.TELNET_ADMIN;
            }
            else
            {
                rank = Rank.SUPER_ADMIN;
            }
            List<String> ips = asec.getStringList("ips");
            String loginMessage = asec.getString("custom_login_message");
            boolean active = asec.getBoolean("is_activated");

            Admin admin = new Admin(username);
            admin.setName(username);
            admin.setRank(rank);
            admin.addIps(ips);
            admin.setLoginMessage(loginMessage);
            admin.setActive(active);
            admin.setLastLogin(new Date());
            conversions.add(admin);
        }

        YamlConfig newYaml = new YamlConfig(plugin, AdminList.CONFIG_FILENAME);
        for (Admin admin : conversions)
        {
            admin.saveTo(newYaml.createSection(admin.getName().toLowerCase()));
        }
        newYaml.save();

        logger.info("Converted " + conversions.size() + " admins");
    }

    private void convertPermbans(File oldFile)
    {
        if (!oldFile.exists())
        {
            logger.warning("No old permban list found!");
            return;
        }

        try
        {
            Files.copy(oldFile, new File(plugin.getDataFolder(), PermbanList.CONFIG_FILENAME));
            logger.info("Converted permban list");
        }
        catch (IOException ex)
        {
            logger.warning("Could not copy old permban list!");
        }

    }

}
