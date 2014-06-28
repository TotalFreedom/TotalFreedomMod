package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Provides uuid tools", usage = "/<command> recalculate admin")
public class Command_uuid extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        if ("recalculate".equals(args[0]))
        {

            if ("admin".equals(args[1]))
            {


                for (TFM_Admin admin : TFM_AdminList.getAllAdmins())
                {
                    final UUID original = admin.getUniqueId();


                }

                return true;
            }


            return false;
        }

        return false;
    }
}
