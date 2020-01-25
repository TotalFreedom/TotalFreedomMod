package me.totalfreedom.totalfreedommod.playerverification;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class VPlayer implements ConfigLoadable, ConfigSavable, Validatable
{

    private final List<String> ips = Lists.newArrayList();
    private final List<Map<?, ?>> notes = Lists.newArrayList();
    private final List<String> backupCodes = Lists.newArrayList();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String discordId = null;
    @Getter
    @Setter
    private String forumUsername = null;
    @Getter
    @Setter
    private Boolean enabled = false;
    @Getter
    @Setter
    private String tag = null;
    @Getter
    @Setter
    private boolean clearChatOptOut = false;
    @Getter
    @Setter
    private String rideMode = "ask";
    @Getter
    @Setter
    private int utcOffset = 0;
    @Getter
    @Setter
    private boolean realTime = false;

    public VPlayer(String name)
    {
        this.name = name;
    }

    public VPlayer(Player player)
    {
        this(player.getName());
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        name = cs.getString("name", name);
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        notes.clear();
        notes.addAll(cs.getMapList("notes"));
        backupCodes.clear();
        backupCodes.addAll(cs.getStringList("backupCodes"));
        discordId = cs.getString("discordId", null);
        enabled = cs.getBoolean("enabled", false);
        tag = cs.getString("tag", null);
        clearChatOptOut = cs.getBoolean("clearChatOptOut", false);
        rideMode = cs.getString("rideMode", rideMode);
        utcOffset = cs.getInt("utcOffset", 0);
        realTime = cs.getBoolean("realTime", false);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save player verification entry: " + name + ". Entry not valid!");
        cs.set("name", name);
        cs.set("discordId", discordId);
        cs.set("enabled", enabled);
        cs.set("tag", tag);
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("notes", Lists.newArrayList(notes));
        cs.set("backupCodes", Lists.newArrayList(backupCodes));
        cs.set("clearChatOptOut", clearChatOptOut);
        cs.set("rideMode", rideMode);
        cs.set("utcOffset", utcOffset);
        cs.set("realTime", realTime);
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

    public List<Map<?, ?>> getNotes()
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
        FLog.info("fuck");
        backupCodes.remove(code);
    }

    public void addNote(String adder, String note)
    {

        Map<String, String> noteMap = new HashMap<>();
        noteMap.put(adder, note);
        notes.add(noteMap);
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

    @Override
    public boolean isValid()
    {
        return name != null && !ips.isEmpty();
    }
}
