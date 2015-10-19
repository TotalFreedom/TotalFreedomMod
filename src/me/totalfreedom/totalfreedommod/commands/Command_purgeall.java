package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Superadmin command - Purge everything! (except for bans).", usage = "/<command>")
public class Command_purgeall extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Purging all player data", true);

        // Purge entities
        FUtil.TFM_EntityWiper.wipeEntities(true, true);

        for (Player player : server.getOnlinePlayers())
        {
            FPlayer playerdata = plugin.pl.getPlayer(player);

            // Unmute all players
            if (playerdata.isMuted())
            {
                playerdata.setMuted(false);
            }

            // Unblock all commands
            if (playerdata.allCommandsBlocked())
            {
                playerdata.setCommandsBlocked(false);
            }

            // Unhalt all players
            if (playerdata.isHalted())
            {
                playerdata.setHalted(false);
            }

            // Stop orbiting
            if (playerdata.isOrbiting())
            {
                playerdata.stopOrbiting();
            }

            // Unfreeze
            if (playerdata.isFrozen())
            {
                playerdata.setFrozen(false);
            }

            // Purge potion effects
            for (PotionEffect potion_effect : player.getActivePotionEffects())
            {
                player.removePotionEffect(potion_effect.getType());
            }

            // Uncage
            if (playerdata.isCaged())
            {
                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();
            }
        }

        // Unfreeze all players
        Command_fr.setAllFrozen(false);

        // Remove all mobs
        Command_mp.purgeMobs();

        return true;
    }
}
