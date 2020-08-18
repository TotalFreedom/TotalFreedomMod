package me.totalfreedom.totalfreedommod;

import java.io.File;
import me.totalfreedom.totalfreedommod.banning.IndefiniteBanList;
import me.totalfreedom.totalfreedommod.config.YamlConfig;
import me.totalfreedom.totalfreedommod.permissions.PermissionConfig;
import me.totalfreedom.totalfreedommod.punishments.PunishmentList;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.util.FileUtil;

public class BackupManager extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public void createBackups(String file)
    {
        createBackups(file, false);
    }

    public void createAllBackups()
    {
        createBackups(TotalFreedomMod.CONFIG_FILENAME, true);
        createBackups(IndefiniteBanList.CONFIG_FILENAME);
        createBackups(PermissionConfig.PERMISSIONS_FILENAME, true);
        createBackups(PunishmentList.CONFIG_FILENAME);
        createBackups("database.db");
    }

    public void createBackups(String file, boolean onlyWeekly)
    {
        final String save = file.split("\\.")[0];
        final YamlConfig config = new YamlConfig(plugin, "backup/backup.yml", false);
        config.load();

        // Weekly
        if (!config.isInt(save + ".weekly"))
        {
            performBackup(file, "weekly");
            config.set(save + ".weekly", FUtil.getUnixTime());
        }
        else
        {
            int lastBackupWeekly = config.getInt(save + ".weekly");

            if (lastBackupWeekly + 3600 * 24 * 7 < FUtil.getUnixTime())
            {
                performBackup(file, "weekly");
                config.set(save + ".weekly", FUtil.getUnixTime());
            }
        }

        if (onlyWeekly)
        {
            config.save();
            return;
        }

        // Daily
        if (!config.isInt(save + ".daily"))
        {
            performBackup(file, "daily");
            config.set(save + ".daily", FUtil.getUnixTime());
        }
        else
        {
            int lastBackupDaily = config.getInt(save + ".daily");

            if (lastBackupDaily + 3600 * 24 < FUtil.getUnixTime())
            {
                performBackup(file, "daily");
                config.set(save + ".daily", FUtil.getUnixTime());
            }
        }

        config.save();
    }

    private void performBackup(String file, String type)
    {
        FLog.info("Backing up " + file + " to " + file + "." + type + ".bak");
        final File backupFolder = new File(plugin.getDataFolder(), "backup");

        if (!backupFolder.exists())
        {
            backupFolder.mkdirs();
        }

        final File oldYaml = new File(plugin.getDataFolder(), file);
        final File newYaml = new File(backupFolder, file + "." + type + ".bak");
        FileUtil.copy(oldYaml, newYaml);
    }

}
