package com.krithel.techmessenger.messagelist.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krithel.techmessenger.R;
import com.krithel.techmessenger.messagelist.dto.MessageData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Krithel on 04-Mar-16.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private List<MessageData> conversationList;
    private OnItemSelectListener listener;

    public MessageListAdapter(List<MessageData> conversationList, OnItemSelectListener listener) {
        this.conversationList = conversationList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_conversation_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessageData c = conversationList.get(position);
        holder.userText.setText(String.format("%s %s", c.getUser().getFirstName(), c.getUser().getSurname()));
        holder.latestMessage.setText(c.getLatestMessage());
        holder.dateSent.setText(c.getDateSent().toString());
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemSelected(c);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public interface OnItemSelectListener {
        void onItemSelected(MessageData md);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View parentView;
        @Bind(R.id.conversation_item_username)
        public TextView userText;
        @Bind(R.id.conversation_item_message)
        public TextView latestMessage;
        @Bind(R.id.conversation_item_time_sent)
        public TextView dateSent;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            this.parentView = v;
        }
    }
}
