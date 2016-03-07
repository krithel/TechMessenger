package com.krithel.techmessenger.util;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.krithel.techmessenger.model.User;

/**
 * Created by Krithel on 07-Mar-16.
 */
public class FirebaseUtil {

    public static void setupCurrentUser(String uuid, final UserCallback callback) {
        // Get the current user
        Firebase fb = new Firebase("https://tech-messenger.firebaseio.com").getRef().child("users").child(uuid);
        fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onUserFound(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                callback.onUserSearchFailure(firebaseError);
            }
        });
    }

    public interface UserCallback {
        void onUserFound(User user);

        void onUserSearchFailure(FirebaseError firebaseError);
    }
}
