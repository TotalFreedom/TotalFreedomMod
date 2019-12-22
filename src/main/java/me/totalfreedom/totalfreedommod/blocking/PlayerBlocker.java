package me.totalfreedom.totalfreedommod.blocking;

import com.google.common.collect.Lists;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class PlayerBlocker extends FreedomService
{

    public static final List<String> blockedTags = Lists.newArrayList();

    public PlayerBlocker(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        // Load banned tags
        blockedTags.clear();
        blockedTags.addAll((Collection<? extends String>) ConfigEntry.BLOCKED_TAGS.getList());
        FLog.info("Loaded " + blockedTags.size() + " banned tags.");
    }

    @Override
    protected void onStop()
    {
    }

}
