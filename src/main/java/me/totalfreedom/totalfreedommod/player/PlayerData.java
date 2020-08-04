package me.totalfreedom.totalfreedommod.player;

import com.google.common.collect.Lists;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerData
{

    @Getter
    @Setter
    private String name;
    private final List<String> ips = Lists.newArrayList();
    private final List<String> notes = Lists.newArrayList();
    @Getter
    @Setter
    private String tag = null;
    @Getter
    @Setter
    private String discordID = null;
    private final List<String> backupCodes = Lists.newArrayList();
    @Setter
    private boolean donator = false;
    @Setter
    private Boolean masterBuilder = false;
    @Setter
    private Boolean verification = false;
    @Getter
    @Setter
    private String rideMode = "ask";
    @Getter
    @Setter
    private int coins;
    private List<String> items = Lists.newArrayList();
    @Getter
    @Setter
    private int totalVotes;
    @Setter
    private boolean displayDiscord = true;
    @Getter
    @Setter
    private String redditUsername;

    public PlayerData(ResultSet resultSet)
    {
        try
        {
            name = resultSet.getString("username");
            ips.clear();
            ips.addAll(FUtil.stringToList(resultSet.getString("ips")));
            notes.clear();
            notes.addAll(FUtil.stringToList(resultSet.getString("notes")));
            tag = resultSet.getString("tag");
            discordID = resultSet.getString("discord_id");
            backupCodes.clear();
            backupCodes.addAll(FUtil.stringToList(resultSet.getString("backup_codes")));
            donator = resultSet.getBoolean("donator");
            masterBuilder = resultSet.getBoolean("master_builder");
            verification = resultSet.getBoolean("verification");
            rideMode = resultSet.getString("ride_mode");
            coins = resultSet.getInt("coins");
            items.clear();
            items.addAll(FUtil.stringToList(resultSet.getString("items")));
            totalVotes = resultSet.getInt("total_votes");
            displayDiscord = resultSet.getBoolean("display_discord");
            redditUsername = resultSet.getString("reddit_username");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load player: " + e.getMessage());
        }

        // Force verification for Master Builders
        if (masterBuilder && !verification)
        {
            verification = true;
            TotalFreedomMod.plugin().pl.save(this);
        }
        else if (!masterBuilder && discordID == null && verification)
        {
            this.verification = false;
            TotalFreedomMod.plugin().pl.save(this);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("Player: ").append(name).append("\n")
                .append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n")
                .append("- Discord ID: ").append(discordID).append("\n")
                .append("- Donator: ").append(donator).append("\n")
                .append("- Master Builder: ").append(masterBuilder).append("\n")
                .append("- Has Verification: ").append(verification).append("\n")
                .append("- Coins: ").append(coins).append("\n")
                .append("- Total Votes: ").append(totalVotes).append("\n")
                .append("- Display Discord: ").append(displayDiscord).append("\n")
                .append("- Tag: ").append(FUtil.colorize(tag)).append(ChatColor.GRAY).append("\n")
                .append("- Ride Mode: ").append(rideMode).append("\n")
                .append("- Backup Codes: ").append(backupCodes.size()).append("/10").append("\n")
                .append("- Reddit Username: ").append(redditUsername);

        return output.toString();
    }

    public PlayerData(Player player)
    {
        this.name = player.getName();
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public boolean addIp(String ip)
    {
        return !ips.contains(ip) && ips.add(ip);
    }

    public void removeIp(String ip)
    {
        ips.remove(ip);
    }

    public void clearIps()
    {
        ips.clear();
    }

    public void addIps(List<String> ips)
    {
        ips.addAll(ips);
    }

    public List<String> getNotes()
    {
        return Collections.unmodifiableList(notes);
    }

    public void clearNotes()
    {
        notes.clear();
    }

    public List<String> getBackupCodes()
    {
        return Collections.unmodifiableList(backupCodes);
    }

    public void setBackupCodes(List<String> codes)
    {
        backupCodes.clear();
        backupCodes.addAll(codes);
    }

    public void removeBackupCode(String code)
    {
        backupCodes.remove(code);
    }

    public void addNote(String note)
    {
        notes.add(note);
    }

    public boolean removeNote(int id) throws IndexOutOfBoundsException
    {
        try
        {
            notes.remove(id);
        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
        return true;
    }

    public void giveItem(ShopItem item)
    {
        items.add(item.getDataName());
    }

    public List<String> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public boolean hasItem(ShopItem item)
    {
        if (items.contains(item.getDataName()))
        {
            return true;
        }
        return false;
    }

    public void removeItem(ShopItem item)
    {
        items.remove(item.getDataName());
    }

    public boolean hasVerification()
    {
        return verification;
    }

    public boolean isDonator()
    {
        return donator;
    }

    public boolean isMasterBuilder()
    {
        return masterBuilder;
    }

    public Map<String, Object> toSQLStorable()
    {
        Map<String, Object> map = new HashMap<String, Object>()
        {{
            put("username", name);
            put("ips", FUtil.listToString(ips));
            put("notes", FUtil.listToString(notes));
            put("tag", tag);
            put("discord_id", discordID);
            put("backup_codes", FUtil.listToString(backupCodes));
            put("donator", donator);
            put("master_builder", masterBuilder);
            put("verification", verification);
            put("ride_mode", rideMode);
            put("coins", coins);
            put("items", FUtil.listToString(items));
            put("total_votes", totalVotes);
            put("display_discord", displayDiscord);
            put("reddit_username", redditUsername);
        }};
        return map;
    }

    public boolean doesDisplayDiscord()
    {
        return displayDiscord;
    }
}
