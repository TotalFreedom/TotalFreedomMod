package me.unraveledmc.unraveledmcmod;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import me.unraveledmc.unraveledmcmod.admin.Admin;
import me.unraveledmc.unraveledmcmod.admin.AdminList;
import me.unraveledmc.unraveledmcmod.banning.PermbanList;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import net.pravian.aero.component.PluginComponent;
import net.pravian.aero.config.YamlConfig;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigConverter extends PluginComponent<UnraveledMCMod>
{

    public static final int CURRENT_CONFIG_VERSION = 1;

    public ConfigConverter(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    public void convert()
    {
        File data = plugin.getDataFolder();
        data.mkdirs();

        boolean convert = false;

        if (!convert)
        {
            return;
        }

        logger.warning("Converting old configs to new format...");

        File backup = new File(data, "backup_old_format");
        backup.mkdirs();

        for (File file : data.listFiles())
        {

            try
            {
                Files.move(file, new File(backup, file.getName()));
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Could not backup file: {0}", file.getName());
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
                logger.log(Level.WARNING, "Invalid superadmin format for admin: {0}", uuid);
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

        logger.log(Level.INFO, "Converted {0} admins", conversions.size());
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
