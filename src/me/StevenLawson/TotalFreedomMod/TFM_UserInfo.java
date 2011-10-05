package me.StevenLawson.TotalFreedomMod;

public class TFM_UserInfo
{
    private boolean user_frozen = false;
    private int msg_count = 0;
    private int block_destroy_total = 0;
    
    private int freecam_destroy_count = 0;
    private int freecam_place_count = 0;

    public TFM_UserInfo()
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
        this.block_destroy_total++;
    }

    public int getBlockDestroyCount()
    {
        return this.block_destroy_total;
    }

    public void resetBlockDestroyCount()
    {
        this.block_destroy_total = 0;
    }
    
    public void incrementFreecamDestroyCount()
    {
        this.freecam_destroy_count++;
    }

    public int getFreecamDestroyCount()
    {
        return this.freecam_destroy_count;
    }

    public void resetFreecamDestroyCount()
    {
        this.freecam_destroy_count = 0;
    }
    
    public void incrementFreecamPlaceCount()
    {
        this.freecam_place_count++;
    }

    public int getFreecamPlaceCount()
    {
        return this.freecam_place_count;
    }

    public void resetFreecamPlaceCount()
    {
        this.freecam_place_count = 0;
    }
}
