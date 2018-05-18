package me.totalfreedom.totalfreedommod.amp;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;


public class AMP extends FreedomService
{
    public AMPManager ampManager;
    public Boolean enabled = false;

    public AMP(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        if(!plugin.config.getBoolean(ConfigEntry.AMP_ENABLED))
        {
            return;
        }
        ampManager = new AMPManager(plugin, plugin.config.getString(ConfigEntry.AMP_URL), plugin.config.getString(ConfigEntry.AMP_USERNAME), plugin.config.getString(ConfigEntry.AMP_PASSWORD));
        LoginCallback callback = new LoginCallback()
        {
            @Override
            public void loginDone(boolean success)
            {
                enabled = success;
            }
        };
        ampManager.connectAsync(callback);

    }

    public void restartServer()
    {
        ampManager.restartAsync();
    }

    @Override
    protected void onStop() {

    }
}
