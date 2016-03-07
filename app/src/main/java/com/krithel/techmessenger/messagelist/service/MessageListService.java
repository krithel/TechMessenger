package com.krithel.techmessenger.messagelist.service;

import com.krithel.techmessenger.model.Conversation;

import java.util.List;

/**
 * Created by Krithel on 04-Mar-16.
 */
public interface MessageListService {

    List<Conversation> getUserConversations(String uuid);
}
