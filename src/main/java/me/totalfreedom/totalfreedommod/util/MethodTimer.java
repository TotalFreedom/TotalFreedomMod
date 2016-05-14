package me.totalfreedom.totalfreedommod.util;

public class MethodTimer
{

    private long lastStart;
    private long total = 0;

    public MethodTimer()
    {
    }

    public void start()
    {
        this.lastStart = System.currentTimeMillis();
    }

    public void update()
    {
        this.total += (System.currentTimeMillis() - this.lastStart);
    }

    public long getTotal()
    {
        return this.total;
    }

    public void printTotalToLog(String timerName)
    {
        FLog.info("DEBUG: " + timerName + " used " + this.getTotal() + " ms.");
    }
}
