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
    public static final Material BLOCK_ID = Material.WOOL;
    public static final double DAMPING_COEFFICIENT = 0.8;
    public final Map<Player, Boolean> canPushMap = new HashMap<Player, Boolean>();
    private JumpPadMode mode = JumpPadMode.OFF;
    private double strength = 0.4;

    public void PlayerMoveEvent(PlayerMoveEvent event)
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
            Boolean canPush = canPushMap.get(player);
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
            canPushMap.put(player, canPush);
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

    public JumpPadMode getMode()
    {
        return mode;
    }

    public void setMode(JumpPadMode mode)
    {
        this.mode = mode;
    }

    public double getStrength()
    {
        return strength;
    }

    public void setStrength(double strength)
    {
        this.strength = strength;
    }

    public static enum JumpPadMode
    {
        OFF(false), NORMAL(true), NORMAL_AND_SIDEWAYS(true), MADGEEK(true);
        private boolean on;

        JumpPadMode(boolean on)
        {
            this.on = on;
        }

        public boolean isOn()
        {
            return on;
        }
    }

    public static TFM_Jumppads getInstance()
    {
        return TFM_JumpadsHolder.INSTANCE;
    }

    private static class TFM_JumpadsHolder
    {
        private static final TFM_Jumppads INSTANCE = new TFM_Jumppads();
    }
}
