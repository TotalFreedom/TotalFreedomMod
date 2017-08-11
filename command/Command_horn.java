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

public class UnicornHorn implements CommandExecutor {



    @CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)

    @CommandParameters(description = "Gives you a unicorn horn for you to put on year head. Neat.", usage = "/<command>")

    public UnicornHorn() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("horn")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.END_ROD);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.RED + ChatColor.ITALIC.toString() + "A nice little unicorn horn. Use /hat to put it on your head!");
                lore.add(" ");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "♫" + ChatColor.AQUA + " Unicorn Horn " + ChatColor.LIGHT_PURPLE + "♫");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.YELLOW + "Use /hat to put it on your head!");
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
