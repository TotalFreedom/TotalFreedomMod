package me.totalfreedom.totalfreedommod.fun;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Jumppads extends FreedomService
{

    public static final Material BLOCK_ID = Material.WOOL;
    public static final double DAMPING_COEFFICIENT = 0.8;
    //
    private final Map<Player, Boolean> pushMap = Maps.newHashMap();
    //
    @Getter
    @Setter
    private JumpPadMode mode = JumpPadMode.MADGEEK;
    @Getter
    @Setter
    private double strength = 0.4;

    public Jumppads(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public void onStop()
    {

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
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
            Boolean canPush = pushMap.get(player);
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
            pushMap.put(player, canPush);
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

    public static enum JumpPadMode
    {

        OFF(false), NORMAL(true), NORMAL_AND_SIDEWAYS(true), MADGEEK(true);
        private final boolean on;

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
