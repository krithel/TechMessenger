package com.krithel.techmessenger.login.model;

import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * Created by Krithel on 03-Mar-16.
 */
public class LoginAttempt {

    private boolean successful;
    private FirebaseError error;
    private Map<String, Object> result;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public FirebaseError getError() {
        return error;
    }

    public void setError(FirebaseError error) {
        this.error = error;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
