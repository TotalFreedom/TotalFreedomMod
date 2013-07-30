package me.StevenLawson.TotalFreedomMod;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TFM_GameRuleHandler
{
    private static final EnumMap<TFM_GameRule, TFM_GameRule_Value> GAME_RULES = new EnumMap<TFM_GameRule, TFM_GameRule_Value>(TFM_GameRule.class);

    static
    {
        for (TFM_GameRule gameRule : TFM_GameRule.values())
        {
            GAME_RULES.put(gameRule, gameRule.getDefaultValue());
        }
    }

    private TFM_GameRuleHandler()
    {
        throw new AssertionError();
    }

    public static void setGameRule(TFM_GameRule gameRule, boolean value)
    {
        setGameRule(gameRule, value, true);
    }

    public static void setGameRule(TFM_GameRule gameRule, boolean value, boolean doCommit)
    {
        GAME_RULES.put(gameRule, TFM_GameRule_Value.fromBoolean(value));
        if (doCommit)
        {
            commitGameRules();
        }
    }

    public static void commitGameRules()
    {
        List<World> worlds = Bukkit.getWorlds();
        Iterator<Map.Entry<TFM_GameRule, TFM_GameRule_Value>> it = GAME_RULES.entrySet().iterator();
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
                    TFM_Util.setWorldTime(world, 6000L);
                }
            }
        }
    }

    public enum TFM_GameRule
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

    public enum TFM_GameRule_Value
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
            return (this.value.equals(TFM_GameRule_Value.TRUE.value) ? true : false);
        }

        public static TFM_GameRule_Value fromBoolean(boolean in)
        {
            return (in ? TFM_GameRule_Value.TRUE : TFM_GameRule_Value.FALSE);
        }
    }
}
