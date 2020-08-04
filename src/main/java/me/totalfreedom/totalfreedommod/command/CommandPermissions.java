package me.totalfreedom.totalfreedommod.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.totalfreedom.totalfreedommod.rank.Rank;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermissions
{

    Rank level() default Rank.NON_OP;

    SourceType source() default SourceType.BOTH;

    boolean blockHostConsole() default false;

    int cooldown() default 0;
}