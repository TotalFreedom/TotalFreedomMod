package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class TFM_ProtectedArea
{
    public static final double MAX_RADIUS = 50.0;
    private static final Map<String, SerializableProtectedRegion> PROTECTED_AREAS = new HashMap<String, SerializableProtectedRegion>();

    private TFM_ProtectedArea()
    {
        throw new AssertionError();
    }

    public static boolean isInProtectedArea(final Location modifyLocation)
    {
        boolean doSave = false;
        boolean inProtectedArea = false;

        final Iterator<Map.Entry<String, SerializableProtectedRegion>> it = TFM_ProtectedArea.PROTECTED_AREAS.entrySet().iterator();

        while (it.hasNext())
        {
            final SerializableProtectedRegion region = it.next().getValue();

            Location regionCenter = null;
            try
            {
                regionCenter = region.getLocation();
            }
            catch (SerializableProtectedRegion.CantFindWorldException ex)
            {
                it.remove();
                doSave = true;
                continue;
            }

            if (regionCenter != null)
            {
                if (modifyLocation.getWorld() == regionCenter.getWorld())
                {
                    final double regionRadius = region.getRadius();
                    if (modifyLocation.distanceSquared(regionCenter) <= (regionRadius * regionRadius))
                    {
                        inProtectedArea = true;
                        break;
                    }
                }
            }
        }

        if (doSave)
        {
            saveProtectedAreas();
        }

        return inProtectedArea;
    }

    public static boolean isInProtectedArea(final Vector min, final Vector max, final String worldName)
    {
        boolean doSave = false;
        boolean inProtectedArea = false;

        final Iterator<Map.Entry<String, SerializableProtectedRegion>> it = TFM_ProtectedArea.PROTECTED_AREAS.entrySet().iterator();

        while (it.hasNext())
        {
            final SerializableProtectedRegion region = it.next().getValue();

            Location regionCenter = null;
            try
            {
                regionCenter = region.getLocation();
            }
            catch (SerializableProtectedRegion.CantFindWorldException ex)
            {
                it.remove();
                doSave = true;
                continue;
            }

            if (regionCenter != null)
            {
                if (worldName.equals(regionCenter.getWorld().getName()))
                {
                    if (cubeIntersectsSphere(min, max, regionCenter.toVector(), region.getRadius()))
                    {
                        inProtectedArea = true;
                        break;
                    }
                }
            }
        }

        if (doSave)
        {
            saveProtectedAreas();
        }

        return inProtectedArea;
    }

    private static boolean cubeIntersectsSphere(Vector min, Vector max, Vector sphere, double radius)
    {
        double d = square(radius);

        if (sphere.getX() < min.getX())
        {
            d -= square(sphere.getX() - min.getX());
        }
        else if (sphere.getX() > max.getX())
        {
            d -= square(sphere.getX() - max.getX());
        }
        if (sphere.getY() < min.getY())
        {
            d -= square(sphere.getY() - min.getY());
        }
        else if (sphere.getY() > max.getY())
        {
            d -= square(sphere.getY() - max.getY());
        }
        if (sphere.getZ() < min.getZ())
        {
            d -= square(sphere.getZ() - min.getZ());
        }
        else if (sphere.getZ() > max.getZ())
        {
            d -= square(sphere.getZ() - max.getZ());
        }

        return d > 0;
    }

    private static double square(double v)
    {
        return v * v;
    }

    public static void addProtectedArea(String label, Location location, double radius)
    {
        TFM_ProtectedArea.PROTECTED_AREAS.put(label.toLowerCase(), new SerializableProtectedRegion(location, radius));
        saveProtectedAreas();
    }

    public static void removeProtectedArea(String label)
    {
        TFM_ProtectedArea.PROTECTED_AREAS.remove(label.toLowerCase());
        saveProtectedAreas();
    }

    public static void clearProtectedAreas()
    {
        clearProtectedAreas(true);
    }

    public static void clearProtectedAreas(boolean createSpawnpointProtectedAreas)
    {
        TFM_ProtectedArea.PROTECTED_AREAS.clear();

        if (createSpawnpointProtectedAreas)
        {
            autoAddSpawnpoints();
        }

        saveProtectedAreas();
    }

    public static void cleanProtectedAreas()
    {
        boolean doSave = false;

        final Iterator<Map.Entry<String, SerializableProtectedRegion>> it = TFM_ProtectedArea.PROTECTED_AREAS.entrySet().iterator();

        while (it.hasNext())
        {
            try
            {
                it.next().getValue().getLocation();
            }
            catch (SerializableProtectedRegion.CantFindWorldException ex)
            {
                it.remove();
                doSave = true;
            }
        }

        if (doSave)
        {
            saveProtectedAreas();
        }
    }

    public static Set<String> getProtectedAreaLabels()
    {
        return TFM_ProtectedArea.PROTECTED_AREAS.keySet();
    }

    public static void saveProtectedAreas()
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PROTECTED_AREA_FILE));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(TFM_ProtectedArea.PROTECTED_AREAS);
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
        File input = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PROTECTED_AREA_FILE);
        try
        {
            if (input.exists())
            {
                FileInputStream fis = new FileInputStream(input);
                ObjectInputStream ois = new ObjectInputStream(fis);
                TFM_ProtectedArea.PROTECTED_AREAS.clear();
                TFM_ProtectedArea.PROTECTED_AREAS.putAll((HashMap<String, SerializableProtectedRegion>) ois.readObject());
                ois.close();
                fis.close();
            }
        }
        catch (Exception ex)
        {
            input.delete();
            TFM_Log.severe(ex);
        }

        cleanProtectedAreas();
    }

    public static void autoAddSpawnpoints()
    {
        if (TFM_ConfigEntry.PROTECTAREA_SPAWNPOINTS.getBoolean())
        {
            for (World world : Bukkit.getWorlds())
            {
                TFM_ProtectedArea.addProtectedArea("spawn_" + world.getName(), world.getSpawnLocation(), TFM_ConfigEntry.PROTECTAREA_RADIUS.getDouble());
            }
        }
    }

    public static class SerializableProtectedRegion implements Serializable
    {
        private final double x, y, z;
        private final double radius;
        private final String worldName;
        private final UUID worldUUID;
        private transient Location location = null;

        public SerializableProtectedRegion(final Location location, final double radius)
        {
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.radius = radius;
            this.worldName = location.getWorld().getName();
            this.worldUUID = location.getWorld().getUID();
            this.location = location;
        }

        public Location getLocation() throws CantFindWorldException
        {
            if (this.location == null)
            {
                World world = Bukkit.getWorld(this.worldUUID);

                if (world == null)
                {
                    world = Bukkit.getWorld(this.worldName);
                }

                if (world == null)
                {
                    throw new CantFindWorldException("Can't find world " + this.worldName + ", UUID: " + this.worldUUID.toString());
                }

                location = new Location(world, x, y, z);
            }
            return this.location;
        }

        public double getRadius()
        {
            return radius;
        }

        public static class CantFindWorldException extends Exception
        {
            private static final long serialVersionUID = 1L;

            public CantFindWorldException(String string)
            {
                super(string);
            }
        }
    }
}
