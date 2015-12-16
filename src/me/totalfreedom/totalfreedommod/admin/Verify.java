package me.totalfreedom.totalfreedommod.admin;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.Getter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import org.bukkit.configuration.ConfigurationSection;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;

public class Verify implements ConfigLoadable, ConfigSavable
{
    @Getter
    private String passwordHash = null;
    @Getter
    private String passwordSalt = null;
    @Getter
    private String totpSecret = null;
    //
    private Totp totp = null;

    public boolean hasPassword()
    {
        return passwordHash != null && passwordSalt != null;
    }

    //
    // Password
    //
    public void setPassword(String password)
    {
        passwordSalt = Base32.random();
        passwordHash = hashPassword(password, passwordSalt);
    }

    public boolean verifyPassword(String password)
    {
        if (!hasPassword())
        {
            return false;
        }

        return hashPassword(password, passwordSalt).equals(passwordHash);
    }

    private String hashPassword(String password, String salt)
    {
        return sha512(password + salt);
    }

    private String sha512(String string)
    {
        return Hashing.sha512().newHasher()
                .putString(string, Charsets.UTF_8)
                .hash()
                .toString()
                .toLowerCase();
    }

    //
    // TOTP
    //
    public boolean hasTotpSecret()
    {
        return totpSecret != null;
    }

    public String generateTotpSecret()
    {
        return totpSecret = Base32.random();
    }

    public void clearTotpSecret()
    {
        totpSecret = null;
    }

    private Totp getTotp()
    {
        if (totpSecret == null)
        {
            return null;
        }

        if (totp == null)
        {
            totp = new Totp(totpSecret);
        }

        return totp;
    }

    public boolean verifyTotp(String code)
    {
        Totp verify = getTotp();
        if (verify == null)
        {
            return false;
        }

        return verify.verify(code);
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        passwordHash = cs.getString("password", null);
        passwordSalt = cs.getString("salt", null);
        totpSecret = cs.getString("totp_secret", null);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        cs.set("password", passwordHash);
        cs.set("salt", passwordSalt);
        cs.set("totp_secret", totpSecret);
    }

}
