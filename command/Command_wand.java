import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wand implements CommandExecutor {

    @CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)

    @CommandParameters(description = "Gives you a wizard's wand.", usage = "/<command>")

    public Wand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("wand")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.BLAZE_ROD);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.RED + "Use this wand to strike down your foes!");
                lore.add(" ");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Wizard Wand");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Go, fulfill your mission as a wizard!");
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage("You must be a player to use this.");
            return true;
        }
    }
}