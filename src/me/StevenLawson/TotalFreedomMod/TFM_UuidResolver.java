package me.StevenLawson.TotalFreedomMod;

import com.google.common.collect.ImmutableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

// Credits to evilmidget38
public class TFM_UuidResolver implements Callable<Map<String, UUID>>
{
    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private final JSONParser jsonParser = new JSONParser();
    private final List<String> names;

    public TFM_UuidResolver(List<String> names)
    {
        this.names = ImmutableList.copyOf(names);
    }

    @Override
    public Map<String, UUID> call()
    {
        final Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++)
        {
            try
            {
                final URL url = new URL(PROFILE_URL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                final String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));

                final OutputStream stream = connection.getOutputStream();
                stream.write(body.getBytes());
                stream.flush();
                stream.close();

                final JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

                for (Object profile : array)
                {
                    final JSONObject jsonProfile = (JSONObject) profile;
                    final String id = (String) jsonProfile.get("id");
                    final String name = (String) jsonProfile.get("name");
                    final UUID uuid = UUID.fromString(
                            id.substring(0, 8)
                            + "-" + id.substring(8, 12)
                            + "-" + id.substring(12, 16)
                            + "-" + id.substring(16, 20)
                            + "-" + id.substring(20, 32));
                    uuidMap.put(name, uuid);
                }

                if (i != requests - 1)
                {
                    Thread.sleep(100L);
                }
            }
            catch (Exception ex)
            {
                TFM_Log.severe("Could not resolve UUID(s) of "
                        + StringUtils.join(names.subList(i * 100, Math.min((i + 1) * 100, names.size())), ", "));
                TFM_Log.severe(ex);
            }
        }
        return uuidMap;
    }

    public static UUID getUUIDOf(String name)
    {
        return new TFM_UuidResolver(Arrays.asList(name)).call().get(name);
    }
}
