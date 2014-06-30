package me.StevenLawson.TotalFreedomMod;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class TFM_Jumppads
{
    public static final Material BLOCK_ID;
    public static final double DAMPING_COEFFICIENT;
    public static final Map<Player, Boolean> PUSH_MAP;
    private static JumpPadMode mode;
    private static double strength;

    static
    {
        BLOCK_ID = Material.WOOL;
        DAMPING_COEFFICIENT = 0.8;
        PUSH_MAP = new HashMap<Player, Boolean>();
        mode = JumpPadMode.MADGEEK;
        strength = 0.4;
    }

    public static void PlayerMoveEvent(PlayerMoveEvent event)
    {
        if (mode == JumpPadMode.OFF)
        {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getTo().getBlock();
        final Vector velocity = player.getVelocity().clone();

        if (mode == JumpPadMode.MADGEEK)
        {
            Boolean canPush = PUSH_MAP.get(player);
            if (canPush == null)
            {
                canPush = true;
            }
            if (block.getRelative(0, -1, 0).getType() == BLOCK_ID)
            {
                if (canPush)
                {
                    velocity.multiply(strength + 0.85).multiply(-1.0);
                }
                canPush = false;
            }
            else
            {
                canPush = true;
            }
            PUSH_MAP.put(player, canPush);
        }
        else
        {
            if (block.getRelative(0, -1, 0).getType() == BLOCK_ID)
            {
                velocity.add(new Vector(0.0, strength, 0.0));
            }

            if (mode == JumpPadMode.NORMAL_AND_SIDEWAYS)
            {
                if (block.getRelative(1, 0, 0).getType() == BLOCK_ID)
                {
                    velocity.add(new Vector(-DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (block.getRelative(-1, 0, 0).getType() == BLOCK_ID)
                {
                    velocity.add(new Vector(DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (block.getRelative(0, 0, 1).getType() == BLOCK_ID)
                {
                    velocity.add(new Vector(0.0, 0.0, -DAMPING_COEFFICIENT * strength));
                }

                if (block.getRelative(0, 0, -1).getType() == BLOCK_ID)
                {
                    velocity.add(new Vector(0.0, 0.0, DAMPING_COEFFICIENT * strength));
                }
            }
        }

        if (!player.getVelocity().equals(velocity))
        {
            player.setFallDistance(0.0f);
            player.setVelocity(velocity);
        }
    }

    public static JumpPadMode getMode()
    {
        return mode;
    }

    public static void setMode(JumpPadMode mode)
    {
        TFM_Jumppads.mode = mode;
    }

    public static double getStrength()
    {
        return strength;
    }

    public static void setStrength(double strength)
    {
        TFM_Jumppads.strength = strength;
    }

    public static enum JumpPadMode
    {
        OFF(false), NORMAL(true), NORMAL_AND_SIDEWAYS(true), MADGEEK(true);
        private boolean on;

        private JumpPadMode(boolean on)
        {
            this.on = on;
        }

        public boolean isOn()
        {
            return on;
        }
    }
}
