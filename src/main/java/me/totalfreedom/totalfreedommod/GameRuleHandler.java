package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class GameRuleHandler extends AbstractService<TotalFreedomMod>
{

    private final EnumMap<TFM_GameRule, TFM_GameRule_Value> rules = new EnumMap<TFM_GameRule, TFM_GameRule_Value>(TFM_GameRule.class);

    public GameRuleHandler(TotalFreedomMod plugin)
    {
        super(plugin);

        for (TFM_GameRule gameRule : TFM_GameRule.values())
        {
            rules.put(gameRule, gameRule.getDefaultValue());
        }
    }

    @Override
    protected void onStart()
    {
        setGameRule(TFM_GameRule.DO_DAYLIGHT_CYCLE, !ConfigEntry.DISABLE_NIGHT.getBoolean(), false);
        setGameRule(TFM_GameRule.DO_FIRE_TICK, ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean(), false);
        setGameRule(TFM_GameRule.DO_MOB_LOOT, false, false);
        setGameRule(TFM_GameRule.DO_MOB_SPAWNING, !ConfigEntry.MOB_LIMITER_ENABLED.getBoolean(), false);
        setGameRule(TFM_GameRule.DO_TILE_DROPS, false, false);
        setGameRule(TFM_GameRule.MOB_GRIEFING, false, false);
        setGameRule(TFM_GameRule.NATURAL_REGENERATION, true, false);
        commitGameRules();
    }

    @Override
    protected void onStop()
    {
    }

    public void setGameRule(TFM_GameRule gameRule, boolean value)
    {
        setGameRule(gameRule, value, true);
    }

    public void setGameRule(TFM_GameRule gameRule, boolean value, boolean doCommit)
    {
        rules.put(gameRule, TFM_GameRule_Value.fromBoolean(value));
        if (doCommit)
        {
            commitGameRules();
        }
    }

    public void commitGameRules()
    {
        List<World> worlds = Bukkit.getWorlds();
        Iterator<Map.Entry<TFM_GameRule, TFM_GameRule_Value>> it = rules.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<TFM_GameRule, TFM_GameRule_Value> gameRuleEntry = it.next();
            String gameRuleName = gameRuleEntry.getKey().getGameRuleName();
            String gameRuleValue = gameRuleEntry.getValue().toString();
            for (World world : worlds)
            {
                world.setGameRuleValue(gameRuleName, gameRuleValue);
                if (gameRuleEntry.getKey() == TFM_GameRule.DO_DAYLIGHT_CYCLE && !gameRuleEntry.getValue().toBoolean())
                {
                    FUtil.setWorldTime(world, 6000L);
                }
            }
        }
    }

    public static enum TFM_GameRule
    {

        DO_FIRE_TICK("doFireTick", TFM_GameRule_Value.TRUE),
        MOB_GRIEFING("mobGriefing", TFM_GameRule_Value.TRUE),
        KEEP_INVENTORY("keepInventory", TFM_GameRule_Value.FALSE),
        DO_MOB_SPAWNING("doMobSpawning", TFM_GameRule_Value.TRUE),
        DO_MOB_LOOT("doMobLoot", TFM_GameRule_Value.TRUE),
        DO_TILE_DROPS("doTileDrops", TFM_GameRule_Value.TRUE),
        COMMAND_BLOCK_OUTPUT("commandBlockOutput", TFM_GameRule_Value.TRUE),
        NATURAL_REGENERATION("naturalRegeneration", TFM_GameRule_Value.TRUE),
        DO_DAYLIGHT_CYCLE("doDaylightCycle", TFM_GameRule_Value.TRUE);
        private final String gameRuleName;
        private final TFM_GameRule_Value defaultValue;

        private TFM_GameRule(String gameRuleName, TFM_GameRule_Value defaultValue)
        {
            this.gameRuleName = gameRuleName;
            this.defaultValue = defaultValue;
        }

        public String getGameRuleName()
        {
            return gameRuleName;
        }

        public TFM_GameRule_Value getDefaultValue()
        {
            return defaultValue;
        }
    }

    public static enum TFM_GameRule_Value
    {

        TRUE("true"), FALSE("false");
        private final String value;

        private TFM_GameRule_Value(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return this.value;
        }

        public boolean toBoolean()
        {
            return (this.value.equals(TFM_GameRule_Value.TRUE.value));
        }

        public static TFM_GameRule_Value fromBoolean(boolean in)
        {
            return (in ? TFM_GameRule_Value.TRUE : TFM_GameRule_Value.FALSE);
        }
    }

}
