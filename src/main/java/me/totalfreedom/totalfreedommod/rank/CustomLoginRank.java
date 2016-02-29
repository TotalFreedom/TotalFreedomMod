package me.totalfreedom.totalfreedommod.rank;

public class CustomLoginRank extends RankProxy
{

    private String loginMessage;

    public CustomLoginRank(Rank rank, String loginMessage)
    {
        super(rank);
    }

    @Override
    public String getColoredLoginMessage()
    {
        return loginMessage;
    }

}
