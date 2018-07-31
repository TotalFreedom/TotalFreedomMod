package me.totalfreedom.totalfreedommod.amp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class LoginResult
{

    @SerializedName("result")
    @Expose
    @Getter
    @Setter
    private Integer result;
    @SerializedName("success")
    @Expose
    @Getter
    @Setter
    private Boolean success;
    @SerializedName("permissions")
    @Expose
    @Getter
    @Setter
    private List<String> permissions = null;
    @SerializedName("sessionID")
    @Expose
    @Getter
    @Setter
    private String sessionID;
    @SerializedName("rememberMeToken")
    @Expose
    @Getter
    @Setter
    private String rememberMeToken;
    @SerializedName("gravatarHash")
    @Expose
    @Getter
    @Setter
    private String gravatarHash;
    @SerializedName("username")
    @Expose
    @Getter
    @Setter
    private String username;
    private final static long serialVersionUID = -523050232433919883L;

}
