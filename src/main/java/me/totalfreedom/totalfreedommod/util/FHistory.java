package me.totalfreedom.totalfreedommod.util;

import java.util.Arrays;
import java.util.UUID;

public class FHistory
{

    private final UUID uuid;

    private final FName[] oldNames;

    public FHistory(UUID uuid, FName[] oldNames)
    {
        this.uuid = uuid;
        this.oldNames = oldNames;
        Arrays.sort(this.oldNames);
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
