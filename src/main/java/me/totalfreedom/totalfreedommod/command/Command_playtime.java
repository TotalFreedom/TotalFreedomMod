package me.totalfreedom.totalfreedommod.command;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.ActivityLogEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gets your playtime statistics.", usage = "/<command>")
public class Command_playtime extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        ActivityLogEntry entry = plugin.acl.getActivityLog(playerSender);
        int seconds = entry.getTotalSecondsPlayed();
        int minutes = 0;
        int hours = 0;
        while (seconds >= 60)
        {
            seconds -= 60;
            minutes += 1;
        }
        while (minutes >= 60)
        {
            minutes -= 60;
            hours += 1;
        }
        if (entry.getTimestamps().size() == 0)
        {
            entry.addLogin();
        }
        String lastLoginString = entry.getTimestamps().get(entry.getTimestamps().size() - 1);
        Date currentTime = Date.from(Instant.now());
        lastLoginString = lastLoginString.replace("Login: ", "");
        Date lastLogin = FUtil.stringToDate(lastLoginString);

        long duration = currentTime.getTime() - lastLogin.getTime();
        long cseconds = duration / 1000 % 60;
        long cminutes = duration / (60 * 1000) % 60;
        long chours = duration / (60 * 60 * 1000);
        StringBuilder sb = new StringBuilder()
                .append("Playtime - " + sender.getName() + "\n")
                .append("Current Session: " + chours + " hours, " + cminutes + " minutes, and " + cseconds + " seconds" + "\n")
                .append("Overall: " + hours + " hours, " + minutes + " minutes, and " + seconds + " seconds" + "\n");
        List<String> durations = entry.getDurations();
        if (durations.size() >= 3)
        {
            sb.append("Recent Sessions:");
            for (int i = 0; i < 3; i++)
            {
                sb.append("\n" + " - " + durations.get((durations.size() - 1) - i));
            }
        }
        msg(sb.toString());
        return true;
    }
}
