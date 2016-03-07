package com.krithel.techmessenger.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.krithel.techmessenger.R;

public class MessengerActivity extends AppCompatActivity {

    private String userUuid;
    private String recipientUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userUuid = getIntent().getExtras().getString("uuid");
        recipientUuid = getIntent().getExtras().getString("recipient_uuid");

        setContentView(R.layout.activity_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("uuid", userUuid);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("uuid", userUuid);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        getIntent().putExtra("uuid", userUuid);
        finishActivity(0);
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getRecipientUuid() {
        return recipientUuid;
    }
}
