package me.unraveledmc.unraveledmcmod.rollback;

public enum EntryType
{

    BLOCK_PLACE("placed"),
    BLOCK_BREAK("broke");
    private final String action;

    private EntryType(String action)
    {
        this.action = action;
    }

    @Override
    public String toString()
    {
        return action;
    }
}
