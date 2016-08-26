package me.totalfreedom.totalfreedommod.player;

public enum VerifyStage {

    NONE,
    VERIFY_PASSWORD,
    VERIFY_TOTP,
    SET_PASSWORD_VERIFY_OLD,
    SET_PASSWORD,
    SET_PASSWORD_CONFIRM,
    SET_TOTP_CONFIRM;
}