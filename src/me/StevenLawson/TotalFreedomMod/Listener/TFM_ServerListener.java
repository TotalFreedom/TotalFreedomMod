package me.StevenLawson.TotalFreedomMod.Listener;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.TFM_CommandBlockerNew;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.CommandBlockSetEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class TFM_ServerListener implements Listener
{
    // CommandBlockSetEvent does not exist in "vanilla" Bukkit/CraftBukkit.
    // Comment this method out if you want to compile this without a custom CraftBukkit.
    // Just make sure that enable-command-block=false in server.properties.
    // -Madgeek
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandBlockSet(CommandBlockSetEvent event)
    {
        Player player = event.getPlayer();
        String newCommandRaw = event.getNewCommand();

        if (!TFM_SuperadminList.isSeniorAdmin(player, true))
        {
            player.sendMessage(ChatColor.GRAY + "Only senior admins may set command block commands.");
            event.setCancelled(true);
            return;
        }

        Matcher matcher = Pattern.compile("^/?(\\S+)").matcher(newCommandRaw);
        if (matcher.find())
        {
            String topLevelCommand = matcher.group(1);
            if (topLevelCommand != null)
            {
                topLevelCommand = topLevelCommand.toLowerCase().trim();

                //We need to make it look like the command is coming from the console, so keep the player's name without the Player instance via dummy:
                if (TFM_CommandBlockerNew.getInstance().isCommandBlocked(topLevelCommand, new TFM_ServerListener_DummyCommandSender(player.getName()), false))
                {
                    player.sendMessage(ChatColor.GRAY + "That command is blocked.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRemoteServerCommand(RemoteServerCommandEvent event)
    {
        if (TFM_CommandBlockerNew.getInstance().isCommandBlocked(event.getCommand(), event.getSender()))
        {
            event.setCommand("");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerCommand(ServerCommandEvent event)
    {
        if (TFM_CommandBlockerNew.getInstance().isCommandBlocked(event.getCommand(), event.getSender()))
        {
            event.setCommand("");
        }
    }

    private static class TFM_ServerListener_DummyCommandSender implements CommandSender
    {
        private final String senderName;

        public TFM_ServerListener_DummyCommandSender(String senderName)
        {
            this.senderName = senderName;
        }

        @Override
        public void sendMessage(String message)
        {
        }

        @Override
        public void sendMessage(String[] messages)
        {
        }

        @Override
        public Server getServer()
        {
            return null;
        }

        @Override
        public String getName()
        {
            return senderName;
        }

        @Override
        public boolean isPermissionSet(String name)
        {
            return true;
        }

        @Override
        public boolean isPermissionSet(Permission perm)
        {
            return true;
        }

        @Override
        public boolean hasPermission(String name)
        {
            return true;
        }

        @Override
        public boolean hasPermission(Permission perm)
        {
            return true;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
        {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin)
        {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
        {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int ticks)
        {
            return null;
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment)
        {
        }

        @Override
        public void recalculatePermissions()
        {
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions()
        {
            return null;
        }

        @Override
        public boolean isOp()
        {
            return true;
        }

        @Override
        public void setOp(boolean value)
        {
        }
    }
}
