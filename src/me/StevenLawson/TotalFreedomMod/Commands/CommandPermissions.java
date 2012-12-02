package me.StevenLawson.TotalFreedomMod.Commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermissions
{
    ADMIN_LEVEL level();

    SOURCE_TYPE_ALLOWED source();

    boolean block_host_console() default false;

    boolean ignore_permissions() default true;
}
