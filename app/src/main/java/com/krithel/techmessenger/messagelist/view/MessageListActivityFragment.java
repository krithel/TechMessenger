package com.krithel.techmessenger.messagelist.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.krithel.techmessenger.R;
import com.krithel.techmessenger.messagelist.dto.MessageData;
import com.krithel.techmessenger.messenger.MessengerActivity;
import com.krithel.techmessenger.model.Conversation;
import com.krithel.techmessenger.model.Message;
import com.krithel.techmessenger.model.User;
import com.krithel.techmessenger.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageListActivityFragment extends Fragment implements FirebaseUtil.UserCallback, MessageListAdapter.OnItemSelectListener {

    private static final String LOG_TAG = MessageListActivityFragment.class.getSimpleName();
    @Bind(R.id.message_list)
    RecyclerView conversationList;

    private RecyclerView.LayoutManager layoutManager;
    private MessageListAdapter adapter;
    private Firebase firebase;
    private User currentUser;

    private Map<Conversation, MessageData> latestMessages = new HashMap<>();

    public MessageListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebase = new Firebase("https://tech-messenger.firebaseio.com");

        View v = inflater.inflate(R.layout.fragment_message_list, container, false);
        ButterKnife.bind(this, v);

        layoutManager = new LinearLayoutManager(getContext());
        conversationList.setLayoutManager(layoutManager);
        conversationList.setAdapter(adapter);

        loadLatestMessages();

        return v;
    }

    public void loadLatestMessages() {
        String userUuid = ((MessageListActivity) getActivity()).getUserUuid();
        FirebaseUtil.setupCurrentUser(userUuid, this);
    }

    @Override
    public void onUserFound(User user) {
        // If the user has conversations, load 'em
        this.currentUser = user;
        if (user.getConversations() != null) {
            for (String conversationId : user.getConversations().values()) {
                Query q = firebase.getRef().child("conversations").orderByChild("id").equalTo(conversationId);
                q.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(LOG_TAG, "Conversation added");
                        Conversation c = dataSnapshot.getValue(Conversation.class);
                        loadLatestMessage(c);
                        // Load latest message for this conversation

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(LOG_TAG, "Conversation changed");
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        latestMessages.remove(conversation);

                        loadLatestMessage(conversation);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "Conversation removed");
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        latestMessages.remove(conversation);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Log.d(LOG_TAG, "Conversation moved");
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        throw new FirebaseException(String.format("Error getting conversation - %s", firebaseError.getMessage()));
                    }
                });
            }
        }
    }

    private void loadLatestMessage(final Conversation c) {
        for (String messageId : c.getMessageIds()) {
            Query mq = firebase.getRef().child("messages").orderByChild("messageId").equalTo(messageId);
            mq.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final Message m = dataSnapshot.getValue(Message.class);
                    if (m.getTimeSent().equals(c.getLatestMessage())) {
                        String otherUser = null;
                        for (String uuid : c.getUsersUuids()) {
                            if (!uuid.equals(currentUser.getUuid())) {
                                otherUser = uuid;
                            }
                        }

                        if (otherUser != null) {
                            FirebaseUtil.setupCurrentUser(otherUser, new FirebaseUtil.UserCallback() {
                                @Override
                                public void onUserFound(User user) {
                                    MessageData md = new MessageData();
                                    md.setUser(user);
                                    md.setLatestMessage(m.getMessage());
                                    md.setDateSent(m.getTimeSent());
                                    latestMessages.put(c, md);
                                    adapter = new MessageListAdapter(new ArrayList<>(latestMessages.values()), MessageListActivityFragment.this);
                                    conversationList.setAdapter(adapter);
                                }

                                @Override
                                public void onUserSearchFailure(FirebaseError firebaseError) {
                                    throw new FirebaseException(String.format("Error getting user - %s", firebaseError.getMessage()));
                                }
                            });
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(LOG_TAG, "Child changed");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(LOG_TAG, "Child removed");
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(LOG_TAG, "Child moved");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    throw new FirebaseException(String.format("Error getting latest message - %s", firebaseError.getMessage()));
                }
            });
        }
    }

    @Override
    public void onUserSearchFailure(FirebaseError firebaseError) {
        throw new FirebaseException(String.format("Error getting user - %s", firebaseError.getMessage()));
    }

    @Override
    public void onItemSelected(MessageData md) {
        Intent i = new Intent(getContext(), MessengerActivity.class);
        i.putExtra("uuid", currentUser.getUuid());
        i.putExtra("recipient_uuid", md.getUser().getUuid());
        startActivityForResult(i, 0);
    }
}
