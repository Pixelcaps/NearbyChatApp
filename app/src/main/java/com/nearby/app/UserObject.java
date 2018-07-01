package com.nearby.app;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.Objects;

public class UserObject {
    public static final String MESSAGE_TYPE = "User";
    private static final Gson sGson = new Gson();
    private String mDisplayName;
    private String userAvatarUrl;

    public UserObject(String displayName, String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
        this.mDisplayName = displayName;
    }

    public static Message newNearbyMessage(UserObject userObject) {
        return new Message(sGson.toJson(userObject).getBytes(Charset.forName("UTF-8")), MESSAGE_TYPE);
    }

    public static UserObject fromNearbyMessage(Message message) {
        String nearbyMessageString = new String(message.getContent());
        return sGson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                UserObject.class);
    }

    @Override
    public boolean equals(Object obj) {
        boolean match = false;
        if (obj != null && obj instanceof UserObject) {
            if (Objects.equals(((UserObject) obj).mDisplayName, this.mDisplayName)) match = true;
        }
        return match;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }
}
