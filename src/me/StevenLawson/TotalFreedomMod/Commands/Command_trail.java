package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredListener;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Pretty rainbow trails.", usage = "/<command> [off]")
public class Command_trail extends TFM_Command
{
    private static Listener movementListener = null;
    private static final List<Player> trailPlayers = new ArrayList<Player>();
    private static final Random RANDOM = new Random();

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0 && "off".equals(args[0]))
        {
            trailPlayers.remove(sender_p);

            playerMsg("Trail disabled.");
        }
        else
        {
            if (!trailPlayers.contains(sender_p))
            {
                trailPlayers.add(sender_p);
            }

            playerMsg("Trail enabled. Use \"/trail off\" to disable.");
        }

        if (!trailPlayers.isEmpty())
        {
            registerMovementHandler();
        }
        else
        {
            unregisterMovementHandler();
        }

        return true;
    }

    private static void registerMovementHandler()
    {
        if (getRegisteredListener(movementListener) == null)
        {
            Bukkit.getPluginManager().registerEvents(movementListener = new Listener()
            {
                @EventHandler(priority = EventPriority.NORMAL)
                public void onPlayerMove(PlayerMoveEvent event)
                {
                    Player player = event.getPlayer();
                    if (trailPlayers.contains(player))
                    {
                        Block fromBlock = event.getFrom().getBlock();
                        if (fromBlock.isEmpty())
                        {
                            Block toBlock = event.getTo().getBlock();
                            if (!fromBlock.equals(toBlock))
                            {
                                fromBlock.setType(Material.WOOL);
                                me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator.setData_Block(fromBlock, (byte) RANDOM.nextInt(16));
                            }
                        }
                    }
                }
            }, TotalFreedomMod.plugin);
        }
    }

    private static void unregisterMovementHandler()
    {
        Listener registeredListener = getRegisteredListener(movementListener);
        if (registeredListener != null)
        {
            PlayerMoveEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private static Listener getRegisteredListener(Listener listener)
    {
        RegisteredListener[] registeredListeners = PlayerMoveEvent.getHandlerList().getRegisteredListeners();
        for (RegisteredListener registeredListener : registeredListeners)
        {
            if (registeredListener.getListener() == listener)
            {
                return listener;
            }
        }
        return null;
    }

    public static void startTrail(Player player)
    {
        if (!trailPlayers.contains(player))
        {
            trailPlayers.add(player);
        }

        if (!trailPlayers.isEmpty())
        {
            registerMovementHandler();
        }
    }

    public static void stopTrail(Player player)
    {
        trailPlayers.remove(player);

        if (trailPlayers.isEmpty())
        {
            unregisterMovementHandler();
        }
    }
}
