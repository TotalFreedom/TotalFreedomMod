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

public class Excalibur implements CommandExecutor {

    @CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)

    @CommandParameters(description = "Gives you excalibur.", usage = "/<command>", aliases = "sword")


    public Excalibur() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("excalibur")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.GOLD_SWORD);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.GOLD + ChatColor.ITALIC.toString() + "It has some sort of markings from King Arthur...");
                lore.add(" ");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Excalibur");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.RED + "You have pulled the sword from its stone.");
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