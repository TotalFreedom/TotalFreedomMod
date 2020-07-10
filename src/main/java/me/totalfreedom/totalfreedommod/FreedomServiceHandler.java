package me.totalfreedom.totalfreedommod;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class FreedomServiceHandler
{
    @Getter
    private List<FreedomService> services;

    public FreedomServiceHandler()
    {
        this.services = new ArrayList<>();
    }

    public void add(FreedomService service)
    {
        services.add(service);
    }

    public int getServiceAmount()
    {
        return services.size();
    }
}