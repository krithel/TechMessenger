package com.krithel.techmessenger.messenger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.krithel.techmessenger.R;
import com.krithel.techmessenger.messagelist.dto.MessageData;
import com.krithel.techmessenger.messagelist.view.MessageListAdapter;
import com.krithel.techmessenger.model.Conversation;
import com.krithel.techmessenger.model.Message;
import com.krithel.techmessenger.model.User;
import com.krithel.techmessenger.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessengerActivityFragment extends Fragment implements FirebaseUtil.UserCallback {

    private static final String LOG_TAG = MessengerActivityFragment.class.getSimpleName();

    @Bind(R.id.et_messenger_recipient)
    EditText recipientEntry;
    @Bind(R.id.tv_messenger_recipient)
    TextView recipientText;
    @Bind(R.id.rv_messenger_messages)
    RecyclerView messageList;
    @Bind(R.id.et_message)
    EditText messageText;
    @Bind(R.id.btn_send_message)
    Button sendButton;

    private List<MessageData> messages = new ArrayList<>();
    //TODO This should be elegently rolled into MessageData
    private List<String> loadedMessageIds = new ArrayList<>();

    private Firebase firebase;
    private User currentUser;
    private User recipient;
    private Conversation conversation;

    private RecyclerView.LayoutManager layoutManager;
    private MessageListAdapter adapter;

    public MessengerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messenger, container, false);

        // Setup Firebase connection
        Firebase.setAndroidContext(getContext());
        firebase = new Firebase("https://tech-messenger.firebaseio.com");

        // Bind Views
        ButterKnife.bind(this, v);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        layoutManager = lm;
        messageList.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(messages, null);
        messageList.setAdapter(adapter);
        // Load the current user
        setupCurrentUser(((MessengerActivity) getActivity()).getUserUuid());

        recipientEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Toast.makeText(getContext(), "Edit Text lost focus", Toast.LENGTH_SHORT).show();
                    onRecipientEntered();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                messageText.setText("");
                messageText.clearFocus();
            }
        });

        // If there is a recipient set, load the conversation
        String recipientUuid = ((MessengerActivity) getActivity()).getRecipientUuid();
        if (recipientUuid != null) {
            FirebaseUtil.setupCurrentUser(recipientUuid, new FirebaseUtil.UserCallback() {
                @Override
                public void onUserFound(User user) {
                    onRecipientSelected(user);
                }

                @Override
                public void onUserSearchFailure(FirebaseError firebaseError) {
                    throw new FirebaseException(String.format("Error getting recipient - %s", firebaseError.getMessage()));
                }
            });
        }
        return v;
    }

    private void sendMessage() {
        String m = messageText.getText().toString();
        Message message = new Message();
        message.setMessage(m);
        message.setFromUserUuid(currentUser.getUuid());
        message.setToUserUuid(recipient.getUuid());
        message.setTimeSent(new Date());
        message.setConversationId(conversation.getId());

        Firebase fb = firebase.child("messages").push();
        message.setMessageId(fb.getKey());

        fb.setValue(message);

        conversation.addMessage(message);
        conversation.setLatestMessage(message.getTimeSent());

        Firebase cfb;
        if (conversation.getId() == null) {
            cfb = firebase.child("conversations").push();
            conversation.setId(cfb.getKey());

            firebase.child("users").child(currentUser.getUuid()).child("conversations").child(recipient.getUuid()).setValue(conversation.getId());
            firebase.child("users").child(recipient.getUuid()).child("conversations").child(currentUser.getUuid()).setValue(conversation.getId());
        } else {
            cfb = firebase.child("conversations").child(conversation.getId());
        }

        cfb.setValue(conversation);
    }

    private void onRecipientEntered() {
        String recipientEmail = recipientEntry.getText().toString();

        Query userQuery = firebase.getRef().child("users").orderByChild("email").equalTo(recipientEmail);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.d(LOG_TAG, "No-one found under the selected email");
                    invalidRecipientSelected();
                } else if (dataSnapshot.getChildrenCount() == 1) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    Log.d(LOG_TAG, "User found: " + user.toString());
                    onRecipientSelected(user);
                } else {
                    throw new FirebaseException(String.format("Only 1 user should exist, %s found.", dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Query cancelled: " + firebaseError.getMessage());
            }
        });
    }

    private void setupCurrentUser(String uuid) {
        // Get the current user
        FirebaseUtil.setupCurrentUser(uuid, this);
    }

    private void onRecipientSelected(final User recipient) {
        // Disable recipient entry, as now this screen will be backed by a conversation
        this.recipient = recipient;
        recipientEntry.setVisibility(View.GONE);
        recipientText.setVisibility(View.VISIBLE);
        recipientText.setText(recipient.getFullName());

        // Get the conversation between the user and recipient if it exists
        if (currentUser.getConversations() != null && currentUser.getConversations().containsKey(recipient.getUuid())) {
            String cId = currentUser.getConversations().get(recipient.getUuid());
            Query q = firebase.getRef().child("conversations").orderByChild("id").equalTo(cId);
            q.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(LOG_TAG, "Conversation added");
                    conversation = dataSnapshot.getValue(Conversation.class);

                    for (String messageId : conversation.getMessageIds()) {
                        loadedMessageIds.add(messageId);
                        loadMessage(messageId, false);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(LOG_TAG, "Conversation changed");
                    conversation = dataSnapshot.getValue(Conversation.class);

                    for (String messageId : conversation.getMessageIds()) {
                        if (!loadedMessageIds.contains(messageId)) {
                            loadMessage(messageId, true);
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(LOG_TAG, "Conversation removed");
                    conversation = dataSnapshot.getValue(Conversation.class);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(LOG_TAG, "Conversation moved");
                    conversation = dataSnapshot.getValue(Conversation.class);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    throw new FirebaseException(String.format("Error getting conversation - %s", firebaseError.getMessage()));
                }
            });
        } else {
            conversation = new Conversation();
            List<String> userIds = new ArrayList<>();
            userIds.add(currentUser.getUuid());
            userIds.add(recipient.getUuid());
            conversation.setUsersUuids(userIds);
        }
    }

    private void loadMessage(String messageId, final boolean smoothScroll) {
        Query q = firebase.getRef().child("messages").orderByChild("messageId").equalTo(messageId);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "Message found: adding to list");
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message m = messageSnapshot.getValue(Message.class);
                    MessageData md = new MessageData();
                    md.setDateSent(m.getTimeSent());
                    if (m.getFromUserUuid().equals(currentUser.getUuid())) {
                        md.setUser(currentUser);
                    } else {
                        md.setUser(recipient);
                    }
                    md.setLatestMessage(m.getMessage());
                    messages.add(md);
                }
                Collections.sort(messages, new Comparator<MessageData>() {
                    @Override
                    public int compare(MessageData lhs, MessageData rhs) {
                        return lhs.getDateSent().compareTo(rhs.getDateSent());
                    }
                });

                adapter.notifyDataSetChanged();

                if (smoothScroll) {
                    messageList.smoothScrollToPosition(messages.size() - 1);
                } else {
                    messageList.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                throw new FirebaseException(String.format("Error getting message - %s", firebaseError.getMessage()));
            }
        });
    }

    private void invalidRecipientSelected() {
        recipientEntry.setError("No-one found using that address.");
    }

    @Override
    public void onUserFound(User user) {
        this.currentUser = user;
    }

    @Override
    public void onUserSearchFailure(FirebaseError firebaseError) {
        throw new FirebaseException(String.format("Failed to find user - %s", firebaseError.getMessage()));
    }
}
