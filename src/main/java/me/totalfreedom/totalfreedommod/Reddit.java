package me.totalfreedom.totalfreedommod;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.CurrentFlair;
import net.dean.jraw.models.Flair;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.references.SubredditReference;
import org.bukkit.entity.Player;

public class Reddit extends FreedomService
{
    private final String SUBREDDIT_NAME = ConfigEntry.REDDIT_SUBREDDIT_NAME.getString();
    private final String USERNAME = ConfigEntry.REDDIT_USERNAME.getString();
    private final String PASSWORD = ConfigEntry.REDDIT_PASSWORD.getString();
    private final String CLIENT_ID = ConfigEntry.REDDIT_CLIENT_ID.getString();
    private final String CLIENT_SECRET = ConfigEntry.REDDIT_CLIENT_SECRET.getString();

    private final UserAgent userAgent = new UserAgent("bot", "me.totalfreedom.reddit", plugin.build.version, USERNAME);
    private final Credentials credentials = Credentials.script(USERNAME, PASSWORD, CLIENT_ID, CLIENT_SECRET);

    private RedditClient reddit = null;
    private SubredditReference subreddit = null;

    private HashMap<String, PlayerData> linkCodes = new HashMap<>();
    private HashMap<PlayerData, String> pending = new HashMap<>();

    private Map<Displayable, String> flairList = new HashMap<>();

    private Map<Displayable, String> flairNameList = new HashMap<>();

    private List<Displayable> noFlairDisplays = Arrays.asList(Title.VERIFIED_STAFF, Rank.IMPOSTOR, Rank.NON_OP, Rank.OP);

    public boolean enabled = false;

    @Override
    public void onStart()
    {
        enabled = ConfigEntry.REDDIT_CLIENT_ID.getString() == null;
        if (!enabled)
        {
            return;
        }

        if (reddit == null)
        {
            try
            {
                reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), credentials);
                reddit.setLogHttp(FUtil.inDeveloperMode());
            }
            catch (NoClassDefFoundError e)
            {
                FLog.warning("The JRAW plugin is not installed, therefore the Reddit service cannot start.");
                FLog.warning("To resolve this error, please download the latest JRAW from: https://github.com/TFPatches/Minecraft-JRAW/releases");
                enabled = false;
                return;
            }
            catch (NullPointerException e)
            {
                FLog.warning("Invalid Reddit credentials specified, please double check everything in the config.");
                enabled = false;
                return;
            }
        }

        if (subreddit == null)
        {
            subreddit = reddit.subreddit(SUBREDDIT_NAME);
        }

        loadFlairList();
    }

    @Override
    public void onStop()
    {
    }

    public void setFlair(String username, String flairID)
    {
        List<Flair> flairs = subreddit.userFlairOptions();
        Flair flair = null;
        for (Flair f : flairs)
        {
            if (f.getId().equals(flairID))
            {
                flair = f;
                break;
            }
        }

        if (flair == null)
        {
            return;
        }

        subreddit.otherUserFlair(username).updateToTemplate(flair.getId(), "");
    }

    public void removeFlair(String username)
    {
        subreddit.otherUserFlair(username).updateToTemplate("", "");
    }

    public void sendModMessage(String username, String subject, String body) throws ApiException
    {
        reddit.me().inbox().compose("/r/" + SUBREDDIT_NAME, username, subject, body);
    }

    public String addLinkCode(PlayerData data, String username)
    {
        String code = FUtil.randomAlphanumericString(10);
        linkCodes.put(code, data);
        pending.put(data, username);
        return code;
    }

    public String checkLinkCode(String code)
    {
        PlayerData data = linkCodes.get(code);
        String username = pending.get(data);
        if (data == null || username == null)
        {
            return null;
        }

        linkCodes.remove(code);
        pending.remove(data);

        data.setRedditUsername(username);
        plugin.pl.save(data);

        return username;
    }

    public boolean updateFlair(Player player)
    {
        if (!enabled)
        {
            return false;
        }

        PlayerData data = plugin.pl.getData(player);
        String username = data.getRedditUsername();
        Displayable display = plugin.rm.getDisplay(player);
        if (username == null)
        {
            FLog.debug("No Reddit account");
            return false;
        }

        CurrentFlair currentFlair = subreddit.otherUserFlair(username).current();
        String currentFlairName = currentFlair.getText();
        String currentFlairID = currentFlair.getId();
        String neededFlairID = flairList.get(display);
        String neededFlairName = flairNameList.get(display);

        FLog.debug("Current ID: " + currentFlairID);
        FLog.debug("Needed ID: " + neededFlairID);

        FLog.debug("Current Name: " + currentFlairName);
        FLog.debug("Needed Name: " + neededFlairName);


        // Work around
        //if (currentFlairID == null && neededFlairID != null || currentFlairID != null && neededFlairID != null && !currentFlairID.equals(neededFlairID))
        if (Strings.isNullOrEmpty(currentFlairName) && neededFlairName != null || !Strings.isNullOrEmpty(currentFlairName) && neededFlairName != null && !currentFlairName.equals(neededFlairName))
        {
            FLog.debug("Setting flair");
            setFlair(username, neededFlairID);
            return true;
        }

        if (noFlairDisplays.contains(display) && !Strings.isNullOrEmpty(currentFlairName))
        {
            FLog.debug("Removing flair");
            removeFlair(username);
            return true;
        }

        return false;
    }

    public void loadFlairList()
    {
        flairList.put(Title.OWNER, ConfigEntry.REDDIT_SERVER_OWNER_FLAIR_ID.getString());
        flairList.put(Title.EXECUTIVE, ConfigEntry.REDDIT_EXECUTIVE_FLAIR_ID.getString());
        flairList.put(Title.ASSISTANT_EXECUTIVE, ConfigEntry.REDDIT_ASSISTANT_EXECUTIVE_FLAIR_ID.getString());
        flairList.put(Title.DEVELOPER, ConfigEntry.REDDIT_DEVELOPER_FLAIR_ID.getString());
        flairList.put(Rank.ADMIN, ConfigEntry.REDDIT_ADMIN_FLAIR_ID.getString());
        flairList.put(Rank.MOD, ConfigEntry.REDDIT_MOD_FLAIR_ID.getString());
        flairList.put(Rank.TRIAL_MOD, ConfigEntry.REDDIT_TRIAL_MOD_FLAIR_ID.getString());
        flairList.put(Title.MASTER_BUILDER, ConfigEntry.REDDIT_MASTER_BUILDER_FLAIR_ID.getString());
        flairList.put(Title.DONATOR, ConfigEntry.REDDIT_DONATOR_FLAIR_ID.getString());

        // Work around because the current flair id keeps returning null, either a JRAW bug or a reddit bug
        flairNameList.put(Title.OWNER, "Server Owner");
        flairNameList.put(Title.EXECUTIVE, "Executive");
        flairNameList.put(Title.ASSISTANT_EXECUTIVE, "Assistant Executive");
        flairNameList.put(Title.DEVELOPER, "Developer");
        flairNameList.put(Rank.ADMIN, "Admin");
        flairNameList.put(Rank.MOD, "Mod");
        flairNameList.put(Rank.TRIAL_MOD, "Trial Mod");
        flairNameList.put(Title.MASTER_BUILDER, "Master Builder");
        flairNameList.put(Title.DONATOR, "Premium");
    }
}
