package me.StevenLawson.TotalFreedomMod;

public class TFUserInfo
{
    private boolean user_frozen = false;
    private int msg_count = 0;
    private int bd_count = 0;

    public TFUserInfo()
    {
    }

    public boolean isFrozen()
    {
        return this.user_frozen;
    }

    public void setFrozen(boolean fr)
    {
        this.user_frozen = fr;
    }

    public void resetMsgCount()
    {
        this.msg_count = 0;
    }

    public void incrementMsgCount()
    {
        this.msg_count++;
    }

    public int getMsgCount()
    {
        return this.msg_count;
    }

    public void incrementBlockDestroyCount()
    {
        this.bd_count++;
    }

    public int getBlockDestroyCount()
    {
        return this.bd_count;
    }

    public void resetBlockDestroyCount()
    {
        this.bd_count = 0;
    }
}
