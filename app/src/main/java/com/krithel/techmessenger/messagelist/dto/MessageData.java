package com.krithel.techmessenger.messagelist.dto;

import com.krithel.techmessenger.model.User;

import java.util.Date;

/**
 * Created by Krithel on 04-Mar-16.
 */
public class MessageData {

    private User user;
    private String latestMessage;
    private Date dateSent;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
}
