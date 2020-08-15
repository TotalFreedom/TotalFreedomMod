package me.totalfreedom.totalfreedommod;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import joptsimple.internal.Strings;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AMP extends FreedomService
{

    public String URL = ConfigEntry.AMP_URL.getString();
    private String API_URL = URL + "/API/Core";
    private String USERNAME = ConfigEntry.AMP_USERNAME.getString();
    private String PASSWORD = ConfigEntry.AMP_PASSWORD.getString();
    private String SESSION_ID;

    @Getter
    private boolean enabled = !Strings.isNullOrEmpty(URL);

    private final List<String> headers = Arrays.asList("Accept:application/json");

    public void onStart()
    {
        if (enabled)
        {
            login();
        }
    }

    public void onStop()
    {
        if (enabled)
        {
            logout();
        }
    }

    public void login()
    {
        JSONObject json = new JSONObject();
        json.put("username", USERNAME);
        json.put("password", PASSWORD);
        json.put("token", "");
        json.put("rememberMe", false);

        String response;
        try
        {
            response = FUtil.sendRequest(API_URL + "/Login", "POST", headers, json.toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        JSONObject jsonResponse;
        try
        {
            jsonResponse = (JSONObject)new JSONParser().parse(response);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return;
        }

        Object sessionID = jsonResponse.get("sessionID");
        if (sessionID == null)
        {
            FLog.warning("Invalid AMP credentials have been specified in the config");
            enabled = false;
            return;
        }
        SESSION_ID = sessionID.toString();
        FLog.info("Logged into AMP");
    }

    public void logout()
    {
        JSONObject json = new JSONObject();
        json.put("SESSIONID", SESSION_ID);

        try
        {
            FUtil.sendRequest(API_URL + "/Logout", "POST", headers, json.toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        FLog.info("Logged out of AMP");
    }

    public void updateAccountStatus(StaffMember staffMember)
    {
        String username = staffMember.getAmpUsername();

        if (username == null || !enabled)
        {
            return;
        }

        if (!staffMember.isActive() || staffMember.getRank() != Rank.ADMIN)
        {
            FLog.debug("Disabling amp acc");
            setAccountEnabled(username, false);
            return;
        }

        FLog.debug("Enabling amp acc");
        setAccountEnabled(username, true);
    }

    public void createAccount(String username, String password)
    {
        makeAccount(username);
        setPassword(username, password);
    }

    public void setAccountEnabled(String username, boolean enable)
    {
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("Disabled", !enable);
        json.put("PasswordExpires", false);
        json.put("CannotChangePassword", false);
        json.put("MustChangePassword", false);
        json.put("SESSIONID", SESSION_ID);

        try
        {
            FUtil.sendRequest(API_URL + "/UpdateUserInfo", "POST", headers, json.toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
    }

    private void makeAccount(String username)
    {
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("SESSIONID", SESSION_ID);

        try
        {
            FUtil.sendRequest(API_URL + "/CreateUser", "POST", headers, json.toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
    }

    public void setPassword(String username, String password)
    {
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("NewPassword", password);
        json.put("SESSIONID", SESSION_ID);

        try
        {
            FUtil.sendRequest(API_URL + "/ResetUserPassword", "POST", headers, json.toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
    }
}
