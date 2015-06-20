package me.SteveLawson.TotalFreedomMod.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Use this command to toggle features that help administrate!", usage = "/<command> [on | off]", aliases = "am")
public class Command_administrationmode extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    Player player = (Player) sender;
    if (args.length == 0) {
          player.sendMessage(ChatColor.RED + "Correct Usage is: /administratemode [On / Off]");
          return false;
          }
          
          else if (args[0].equalsIgnoreCase("on")) {
          player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20000, 1));
          player.chat("/ci");
          player.chat("/i stick 1 name:Logstick");
          player.chat("/i wood 1 name:Logblock");
          player.chat("/i clownfish 1 name:Telnet_Clownfish");
          player.chat("/creative");
          player.sendMessage(ChatColor.GREEN + "You have activated administration mode, please do /adminstrationmode off to disable it");
          return true;
          }   
          
          else if (args[0].equalsIgnoreCase("off")) {
          player.chat("/ci");
          player.chat("/heal");
          player.chat("/fly");
          player.sendMessage(ChatColor.RED + You have disable administration mode!");
          return true;
          } 
       
    }      
}
