package me.totalfreedom.totalfreedommod.player;

import com.google.common.collect.Maps;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerList extends FreedomService
{

    @Getter
    public final Map<String, FPlayer> playerMap = Maps.newHashMap(); // ip,dataMap

    @Getter
    public final Map<String, PlayerData> dataMap = Maps.newHashMap(); // username, data

    @Override
    public void onStart()
    {
        dataMap.clear();
        loadMasterBuilders();
    }

    @Override
    public void onStop()
    {
    }

    public FPlayer getPlayerSync(Player player)
    {
        synchronized (playerMap)
        {
            return getPlayer(player);
        }
    }

    public void loadMasterBuilders()
    {
        ResultSet resultSet = plugin.sql.getMasterBuilders();

        if (resultSet == null)
        {
            return;
        }

        try
        {
            while (resultSet.next())
            {
                PlayerData playerData = load(resultSet);
                dataMap.put(playerData.getName(), playerData);
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to parse master builders: " + e.getMessage());
        }
    }

    public String getIp(OfflinePlayer player)
    {
        if (player.isOnline())
        {
            return FUtil.getIp(player.getPlayer());
        }

        final PlayerData entry = getData(player.getName());

        return (entry == null ? null : entry.getIps().iterator().next());
    }

    public List<String> getMasterBuilderNames()
    {
        List<String> masterBuilders = new ArrayList<>();
        for (PlayerData playerData : plugin.pl.dataMap.values())
        {
            if (playerData.isMasterBuilder())
            {
                masterBuilders.add(playerData.getName());
            }
        }
        return masterBuilders;
    }

    public boolean canManageMasterBuilders(String name)
    {
        PlayerData data = getData(name);

        if ((!ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase()) && data != null && !ConfigEntry.SERVER_OWNERS.getStringList().contains(data.getName()))
                && !ConfigEntry.SERVER_EXECUTIVES.getStringList().contains(data.getName())
                && !isTelnetMasterBuilder(data)
                && !ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase()))
        {
            return false;
        }
        return true;
    }

    public boolean isTelnetMasterBuilder(PlayerData playerData)
    {
        StaffMember staffMember = plugin.sl.getEntryByName(playerData.getName());
        if (staffMember != null && staffMember.getRank().isAtLeast(Rank.MOD) && playerData.isMasterBuilder())
        {
            return true;
        }

        return false;
    }

    // May not return null
    public FPlayer getPlayer(Player player)
    {
        FPlayer tPlayer = playerMap.get(FUtil.getIp(player));
        if (tPlayer != null)
        {
            return tPlayer;
        }

        tPlayer = new FPlayer(plugin, player);
        playerMap.put(FUtil.getIp(player), tPlayer);

        return tPlayer;
    }

    public PlayerData loadByName(String name)
    {
        return load(plugin.sql.getPlayerByName(name));
    }

    public PlayerData loadByIp(String ip)
    {
        return load(plugin.sql.getPlayerByIp(ip));
    }

    public PlayerData load(ResultSet resultSet)
    {
        if (resultSet == null)
        {
            return null;
        }
        return new PlayerData(resultSet);
    }

    public Boolean isPlayerImpostor(Player player)
    {
        PlayerData playerData = getData(player);
        return !plugin.sl.isStaff(player)
                && (playerData.hasVerification())
                && !playerData.getIps().contains(FUtil.getIp(player));
    }

    public boolean isImposter(Player player)
    {
        return isPlayerImpostor(player) || plugin.sl.isStaffImpostor(player);
    }

    public void verify(Player player, String backupCode)
    {
        PlayerData playerData = getData(player);
        if (backupCode != null)
        {
            playerData.removeBackupCode(backupCode);
        }

        playerData.addIp(FUtil.getIp(player));
        save(playerData);

        if (plugin.sl.isStaffImpostor(player))
        {
            StaffMember staffMember = plugin.sl.getEntryByName(player.getName());
            staffMember.setLastLogin(new Date());
            staffMember.addIp(FUtil.getIp(player));
            plugin.sl.updateTables();
            plugin.sl.save(staffMember);
        }

        plugin.rm.updateDisplay(player);
    }

    public void syncIps(StaffMember staffMember)
    {
        PlayerData playerData = getData(staffMember.getName());
        playerData.clearIps();
        playerData.addIps(staffMember.getIps());
        plugin.pl.save(playerData);
    }

    public void syncIps(PlayerData playerData)
    {
        StaffMember staffMember = plugin.sl.getEntryByName(playerData.getName());

        if (staffMember != null && staffMember.isActive())
        {
            staffMember.clearIPs();
            staffMember.addIps(playerData.getIps());
            plugin.sl.updateTables();
            plugin.sl.save(staffMember);
        }
    }


    public void save(PlayerData player)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getPlayerByName(player.getName());
            for (Map.Entry<String, Object> entry : player.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());
                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setPlayerValue(player, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save player: " + e.getMessage());
        }
    }

    public PlayerData getData(Player player)
    {
        // Check for existing data
        PlayerData playerData = dataMap.get(player.getName());
        if (playerData != null)
        {
            return playerData;
        }

        // Load data
        playerData = loadByName(player.getName());

        if (playerData == null)
        {
            playerData = loadByIp(FUtil.getIp(player));
            if (playerData != null)
            {
                plugin.sql.updatePlayerName(playerData.getName(), player.getName());
                playerData.setName(player.getName());
                dataMap.put(player.getName(), playerData);
                return playerData;
            }
        }
        else
        {
            dataMap.put(player.getName(), playerData);
            return playerData;
        }

        // Create new data if nonexistent
        if (playerData == null)
        {
            FLog.info("Creating new player verification entry for " + player.getName());

            // Create new player
            playerData = new PlayerData(player);
            playerData.addIp(FUtil.getIp(player));

            // Store player
            dataMap.put(player.getName(), playerData);

            // Save player
            plugin.sql.addPlayer(playerData);
            return playerData;
        }

        return null;
    }

    public PlayerData getData(String username)
    {
        // Check for existing data
        PlayerData playerData = dataMap.get(username);
        if (playerData != null)
        {
            return playerData;
        }

        playerData = loadByName(username);

        if (playerData != null)
        {
            dataMap.put(username, playerData);
        }
        else
        {
            return null;
        }

        return playerData;
    }

    public PlayerData getDataByIp(String ip)
    {
        PlayerData player = loadByIp(ip);

        if (player != null)
        {
            dataMap.put(player.getName(), player);
        }

        return player;
    }
}