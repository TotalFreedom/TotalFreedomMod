package me.totalfreedom.totalfreedommod.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermissions
{

    PlayerRank level();

    SourceType source();

    boolean blockHostConsole() default false;
}
