/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.totalfreedom.totalfreedommod.world;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.component.service.AbstractService;

public class TFM_WorldManager extends AbstractService<TotalFreedomMod>
{

    public TFM_Flatlands flatlands;
    public TFM_AdminWorld adminworld;

    public TFM_WorldManager(TotalFreedomMod plugin)
    {
        super(plugin);

        this.flatlands = new TFM_Flatlands();
    }

    @Override
    protected void onStart()
    {
        flatlands.getWorld();
        adminworld.getWorld();
    }

    @Override
    protected void onStop()
    {

    }

}
