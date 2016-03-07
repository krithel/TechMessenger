package com.krithel.techmessenger.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Krithel on 04-Mar-16.
 */
public class Conversation {

    private String id;
    private List<String> usersUuids;
    private Date latestMessage;
    private List<String> messageIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUsersUuids() {
        return usersUuids;
    }

    public void setUsersUuids(List<String> usersUuids) {
        this.usersUuids = usersUuids;
    }

    public Date getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Date latestMessage) {
        this.latestMessage = latestMessage;
    }

    public List<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    public void addMessage(Message message) {
        if (messageIds == null) {
            messageIds = new ArrayList<String>();
        }
        messageIds.add(message.getMessageId());
    }
}
