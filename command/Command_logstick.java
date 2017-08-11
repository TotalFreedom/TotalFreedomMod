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

public class SuperLogger implements CommandExecutor {

    @CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)

    @CommandParameters(description = "Gives the sender a logstick. Admins only.", usage = "/<command>", aliases = "lstick")


    public SuperLogger() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("logstick")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.STICK);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.GREEN + ChatColor.ITALIC.toString() + "Admins, use this to log blocks!");
                lore.add(" ");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.RED + "Logstick");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.GREEN + "Log, log, log!");
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