package com.krithel.techmessenger.login.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.krithel.techmessenger.R;
import com.krithel.techmessenger.login.model.LoginValidation;
import com.krithel.techmessenger.login.util.LoginUtils;
import com.krithel.techmessenger.model.User;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterFragment extends Fragment {

    private static final String LOG_TAG = RegisterFragment.class.getSimpleName();

    // Bind views
    @Bind(R.id.register_email)
    EditText emailEntry;

    @Bind(R.id.register_password)
    EditText passwordEntry;

    @Bind(R.id.register_first_name)
    EditText firstNameEntry;

    @Bind(R.id.register_last_name)
    EditText lastNameEntry;

    @Bind(R.id.register_button)
    Button registerButton;

    @Bind(R.id.register_email_form)
    View registrationForm;

    @Bind(R.id.register_progress)
    View progressView;

    private View rootView;

    // Firebase
    private Firebase firebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views and bind using ButterKnife
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, rootView);

        // Setup Firebase connection
        Firebase.setAndroidContext(getContext());
        firebase = new Firebase("https://tech-messenger.firebaseio.com");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        return rootView;
    }

    private void attemptRegister() {

        // Reset errors.
        emailEntry.setError(null);
        passwordEntry.setError(null);

        // Store values at the time of the login attempt.
        String email = emailEntry.getText().toString();
        String password = passwordEntry.getText().toString();

        LoginValidation validation = LoginUtils.validateEmailAndPasseword(email, password);

        if (!validation.isValid()) {
            EditText focusView = null;
            switch (validation.getInvalidField()) {
                case EMAIL:
                    focusView = emailEntry;
                    break;
                case PASSWORD:
                    focusView = passwordEntry;
                    break;
            }

            focusView.setError(getString(validation.getErrorResId()));
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {

                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    registrationSuccessful(stringObjectMap);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    registrationFailed(firebaseError);
                }
            });
        }
    }

    private void registrationSuccessful(Map<String, Object> result) {
        // Now save the user object
        String firstName = firstNameEntry.getText().toString();
        String lastName = lastNameEntry.getText().toString();
        String email = emailEntry.getText().toString();
        Log.d(LOG_TAG, String.format("Registered user %s %s under username: %s",
                firstName, lastName, email));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setSurname(lastName);
        newUser.setUuid(result.get("uid").toString());
        newUser.setConversations(new HashMap<String, String>());
        Firebase userRef = firebase.child("users").child(newUser.getUuid());
        userRef.setValue(newUser);

        Toast.makeText(getContext(), "Successfully registered! Please login.", Toast.LENGTH_SHORT).show();
        RegisterFragment.this.getActivity().finish();
    }

    private void registrationFailed(FirebaseError firebaseError) {
        Log.d(LOG_TAG, String.format("Failed to register user %s %s under username: %s",
                firstNameEntry.getText().toString(),
                lastNameEntry.getText().toString(),
                emailEntry.getText().toString()));
        Log.d(LOG_TAG, String.format("Firebase Error: %s - %s", firebaseError.getCode(), firebaseError.getMessage()));

        showProgress(false);
        Snackbar.make(rootView, String.format("Registration failed: %s", firebaseError.getMessage()), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registrationForm.setVisibility(show ? View.GONE : View.VISIBLE);
            registrationForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registrationForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registrationForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
