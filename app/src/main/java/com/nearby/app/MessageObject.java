package com.nearby.app;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

public class MessageObject {
    public static final String MESSAGE_TYPE = "Message";
    public static final String MESSAGE_CONTENT_TEXT = "text";
    public static final String MESSAGE_CONTENT_IMAGE = "image";

    private static final Gson sGson = new Gson();
    private String mDisplayName;
    private String mMessageBody;
    private boolean mFromUser;
    private UUID mMessageUUID;
    private String mMessageContent;

    public MessageObject(String displayName, String messageBody, String messageContent, boolean fromUser) {
        this.mDisplayName = displayName;
        this.mMessageBody = messageBody;
        this.mMessageContent = messageContent;
        this.mFromUser = fromUser;
        this.mMessageUUID = UUID.randomUUID();
    }

    public static Message newNearbyMessage(MessageObject messageObject) {
        return new Message(sGson.toJson(messageObject).getBytes(Charset.forName("UTF-8")),MESSAGE_TYPE);
    }

    public static MessageObject fromNearbyMessage(Message message){
        String nearbyMessageString = new String(message.getContent()).trim();
        return sGson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                MessageObject.class);
    }

    @Override
    public boolean equals(Object obj) {
        boolean match = false;
        if (obj != null && obj instanceof MessageObject){
            if (Objects.equals(((MessageObject) obj).mDisplayName, this.mDisplayName) &&
                    Objects.equals(((MessageObject) obj).mMessageBody, this.mMessageBody) &&
                    Objects.equals(((MessageObject) obj).mMessageContent, this.mMessageContent) &&
                    Objects.equals(((MessageObject) obj).mFromUser, this.mFromUser) &&
                    Objects.equals(((MessageObject) obj).mMessageUUID, this.mMessageUUID)){
                match = true;
            }
        }
        return match;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getMessageBody() {
        return mMessageBody;
    }

    public boolean getFromUser() {
        return mFromUser;
    }

    public UUID getMessageUuid() {
        return mMessageUUID;
    }

    public String getMessageContent(){
        return mMessageContent;
    }

    public void setFromUser(boolean mFromUser) {
        this.mFromUser = mFromUser;
    }

}
