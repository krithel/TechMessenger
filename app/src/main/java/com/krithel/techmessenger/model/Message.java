package com.krithel.techmessenger.model;

import java.util.Date;

/**
 * Created by Krithel on 04-Mar-16.
 */
public class Message {

    private String messageId;
    private String message;
    private Date timeSent;
    private String fromUserUuid;
    private String toUserUuid;
    private String conversationId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public String getFromUserUuid() {
        return fromUserUuid;
    }

    public void setFromUserUuid(String fromUserUuid) {
        this.fromUserUuid = fromUserUuid;
    }

    public String getToUserUuid() {
        return toUserUuid;
    }

    public void setToUserUuid(String toUserUuid) {
        this.toUserUuid = toUserUuid;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
