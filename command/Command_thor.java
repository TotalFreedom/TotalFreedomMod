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

public class ThorHammer implements CommandExecutor {

    @CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)

    @CommandParameters(description = "Gives you Thor's Hammer. Use it wisely.", usage = "/<command>", aliases = "hammer")

    public ThorHammer() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("thor")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.IRON_PICKAXE);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.GOLD + ChatColor.ITALIC.toString() + "Whoever holds this hammer, if he be worthy, shall possess the power of Thor.");
                lore.add(" ");
                lore.add(ChatColor.DARK_RED + "This is the most powerful weapon of all time. Use it with care.");
                lore.add(" ");
                lore.add(" ");
                lore.add(ChatColor.LIGHT_PURPLE + "Effects:");
                lore.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "→" + ChatColor.GRAY + "Smite your foes.");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Thor's Hammer");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.GOLD + "Let thunder rain upon your foes.");
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
    }
}