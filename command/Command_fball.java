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

public class Fireball implements CommandExecutor {

    @CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)

    @CommandParameters(description = "Gives you a little fireball.", usage = "/<command>", aliases = "ball")

    public Fireball() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(label.equalsIgnoreCase("fball")) {
                Player p = (Player)sender;
                ItemStack i = new ItemStack(Material.SNOW_BALL);
                ItemMeta meta = i.getItemMeta();
                List<String> lore = new ArrayList();
                lore.add(ChatColor.RED + "A fireball- but it's actually a snowball.");
                lore.add(" ");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.GOLD + "Fireball");
                i.setItemMeta(meta);
                p.getInventory().addItem(new ItemStack[]{i});
                sender.sendMessage(ChatColor.RED + "Enjoy your fireball! Don't be too harsh with it.");
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