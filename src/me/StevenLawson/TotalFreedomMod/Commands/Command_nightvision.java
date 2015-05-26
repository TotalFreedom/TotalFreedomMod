package me.StevenLawson.TotalFreedomMod.Commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Enables/Disables nightvision.", usage = "/<command>",  aliases = "nv")
public class Command_nightvision extends TFM_Command {

	@Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd,
			String commandLabel, String[] args, boolean senderIsConsole) {
		Player player = (Player) sender;
		if (commandLabel.equalsIgnoreCase("nightvision")) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				player.sendMessage(ChatColor.RED + "NightVision Disabled!");

			} else {
				player.sendMessage(ChatColor.AQUA + "Nightvision Enabled");
				player.addPotionEffect(new PotionEffect(
						PotionEffectType.NIGHT_VISION, 9999999, 1));
				return false;
			}
		}
		return true;
	}
}
