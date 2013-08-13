package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class TFM_Jumppads
{
    final int blockId = Material.WOOL.getId();
    public Mode mode = Mode.OFF;
    public float strength = 0.4F;

    public void PlayerMoveEvent(PlayerMoveEvent event)
    {
        if (mode == Mode.OFF)
        {
            return;
        }

        final Player p = event.getPlayer();
        final Block b = event.getTo().getBlock();
        Vector velocity = p.getVelocity().clone();

        if (b.getRelative(0, -1, 0).getTypeId() == blockId)
        {
            velocity.add(new Vector(0, strength, 0));
        }

        if (mode == Mode.NORMAL_AND_SIDEWAYS)
        {
            if (b.getRelative(1, 0, 0).getTypeId() == blockId)
            {
                velocity.add(new Vector(-0.8F * strength, 0F, 0F));
            }

            if (b.getRelative(-1, 0, 0).getTypeId() == blockId)
            {
                velocity.add(new Vector(0.8F * strength, 0F, 0F));
            }

            if (b.getRelative(0, 0, 1).getTypeId() == blockId)
            {
                velocity.add(new Vector(0F, 0F, -0.8F * strength));
            }

            if (b.getRelative(0, 0, -1).getTypeId() == blockId)
            {
                velocity.add(new Vector(0F, 0F, 0.8F * strength));
            }
        }

        if (!p.getVelocity().equals(velocity))
        {
            p.setFallDistance(0F);
            p.setVelocity(velocity);
        }
    }

    public static enum Mode
    {
        OFF(false), NORMAL(true), NORMAL_AND_SIDEWAYS(true);
        private boolean on;

        Mode(boolean on)
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
