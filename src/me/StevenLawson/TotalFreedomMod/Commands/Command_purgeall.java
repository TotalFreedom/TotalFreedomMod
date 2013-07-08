package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_DisguiseCraftBridge;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Superadmin command - Purge everything! (except for bans).", usage = "/<command>")
public class Command_purgeall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        TFM_Util.adminAction(sender.getName(), "Purging all player data", true);

        // Purge entities
        TFM_Util.wipeEntities(true, true);

        // Undisguise all players
        TFM_DisguiseCraftBridge.getInstance().undisguiseAllPlayers();

        for (Player p : server.getOnlinePlayers())
        {
            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);

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
            for (PotionEffect potion_effect : p.getActivePotionEffects())
            {
                p.removePotionEffect(potion_effect.getType());
            }
        }

        // Clear auto-unmute and auto-unfreeze tasks
        if (TotalFreedomMod.mutePurgeEventId != 0)
        {
            server.getScheduler().cancelTask(TotalFreedomMod.mutePurgeEventId);
            TotalFreedomMod.mutePurgeEventId = 0;
        }
        if (TotalFreedomMod.freezePurgeEventId != 0)
        {
            server.getScheduler().cancelTask(TotalFreedomMod.freezePurgeEventId);
            TotalFreedomMod.freezePurgeEventId = 0;
        }


        // Remove all mobs
        for (World world : server.getWorlds())
        {
            for (Entity ent : world.getLivingEntities())
            {
                if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime || ent instanceof EnderDragon || ent instanceof Ambient)
                {
                    ent.remove();
                }
            }
        }

        return true;
    }
}
