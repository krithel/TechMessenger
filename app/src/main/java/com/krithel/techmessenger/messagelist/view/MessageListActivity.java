package com.krithel.techmessenger.messagelist.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.krithel.techmessenger.R;
import com.krithel.techmessenger.messenger.MessengerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageListActivity extends AppCompatActivity {

    @Bind(R.id.fab_new_conversation)
    FloatingActionButton newConversation;

    private String userUuid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO Extract to a constant for safety
        userUuid = getIntent().getExtras().getString("uuid");
        setContentView(R.layout.activity_message_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newConversation();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("uuid", userUuid);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void newConversation() {
        Intent i = new Intent(this, MessengerActivity.class);
        i.putExtra("uuid", userUuid);
        startActivity(i);
    }

    public String getUserUuid() {
        return userUuid;
    }
}
