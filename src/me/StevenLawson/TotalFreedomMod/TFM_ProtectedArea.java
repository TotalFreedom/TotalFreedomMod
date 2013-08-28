package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class TFM_ProtectedArea implements Serializable
{
    private static final long serialVersionUID = -3270338811000937254L;
    public static final double MAX_RADIUS = 50.0D;
    private static Map<String, TFM_ProtectedArea> protectedAreas = new HashMap<String, TFM_ProtectedArea>();
    private final SerializableLocation center;
    private final double radius;

    private TFM_ProtectedArea(Location root_location, double radius)
    {
        this.center = new SerializableLocation(root_location);
        this.radius = radius;
    }

    public static boolean isInProtectedArea(Location location)
    {
        for (Map.Entry<String, TFM_ProtectedArea> protectedArea : TFM_ProtectedArea.protectedAreas.entrySet())
        {
            Location protectedAreaCenter = SerializableLocation.returnLocation(protectedArea.getValue().center);
            if (protectedAreaCenter != null)
            {
                if (location.getWorld() == protectedAreaCenter.getWorld())
                {
                    double protectedAreaRadius = protectedArea.getValue().radius;

                    if (location.distanceSquared(protectedAreaCenter) <= (protectedAreaRadius * protectedAreaRadius))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void addProtectedArea(String label, Location location, double radius)
    {
        TFM_ProtectedArea.protectedAreas.put(label.toLowerCase(), new TFM_ProtectedArea(location, radius));
        saveProtectedAreas();
    }

    public static void removeProtectedArea(String label)
    {
        TFM_ProtectedArea.protectedAreas.remove(label.toLowerCase());
        saveProtectedAreas();
    }

    public static void clearProtectedAreas()
    {
        TFM_ProtectedArea.protectedAreas.clear();
        autoAddSpawnpoints();
        saveProtectedAreas();
    }

    public static Set<String> getProtectedAreaLabels()
    {
        return TFM_ProtectedArea.protectedAreas.keySet();
    }

    public static void saveProtectedAreas()
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PROTECTED_AREA_FILE));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TFM_ProtectedArea.protectedAreas);
            oos.close();
            fos.close();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadProtectedAreas()
    {
        try
        {
            File input = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PROTECTED_AREA_FILE);
            if (input.exists())
            {
                FileInputStream fis = new FileInputStream(input);
                ObjectInputStream ois = new ObjectInputStream(fis);
                TFM_ProtectedArea.protectedAreas = (HashMap<String, TFM_ProtectedArea>) ois.readObject();
                ois.close();
                fis.close();
            }
        }
        catch (Exception ex)
        {
            File input = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PROTECTED_AREA_FILE);
            input.delete();

            TFM_Log.severe(ex);
        }
    }

    public static void autoAddSpawnpoints()
    {
        if (TFM_ConfigEntry.AUTO_PROTECT_SPAWNPOINTS.getBoolean())
        {
            for (World world : Bukkit.getWorlds())
            {
                TFM_ProtectedArea.addProtectedArea("spawn_" + world.getName(), world.getSpawnLocation(), TFM_ConfigEntry.AUTO_PROTECT_RADIUS.getDouble());
            }
        }
    }
}
