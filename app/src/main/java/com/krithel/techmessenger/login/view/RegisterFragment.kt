package com.krithel.techmessenger.login.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.krithel.techmessenger.R
import com.krithel.techmessenger.model.User
import com.krithel.techmessenger.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class RegisterFragment : Fragment() {

    // Firebase
    private var firebase: Firebase = Firebase("https://tech-messenger.firebaseio.com")

    private var rootView: View? = null


    // Bind views
    private val emailEntry: EditText by bindView(R.id.register_email)
    private val passwordEntry: EditText by bindView(R.id.register_password)
    private val firstNameEntry: EditText by bindView(R.id.register_first_name)
    private val lastNameEntry: EditText by bindView(R.id.register_last_name)
    private val registerButton: Button by bindView(R.id.register_button)
    private val registrationForm: View by bindView(R.id.register_email_form)
    private val progressView: View by bindView(R.id.register_progress)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Setup the base view
        rootView = inflater.inflate(R.layout.fragment_register, container, false)

        // Setup Firebase connection
        Firebase.setAndroidContext(context)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {

        // Reset errors.
        emailEntry.error = null
        passwordEntry.error = null

        // Validate the email and password
        if (emailEntry.enteredText.isEmpty()) {
            emailEntry.error = getString(R.string.error_field_required)
        } else if (!emailEntry.enteredText.isEmail()) {
            emailEntry.error = getString(R.string.error_invalid_email)
        } else if (!passwordEntry.enteredText.isValidPassword()) {
            passwordEntry.error = getString(R.string.error_invalid_password)
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true, registrationForm, progressView)

            firebase.createUser(emailEntry.enteredText,
                    passwordEntry.enteredText,
                    { registrationSuccessful(it) },
                    { registrationFailed(it) })
        }
    }

    private fun registrationSuccessful(result: Map<String, Any>) {
        // Now save the user object
        debug("Registered user ${firstNameEntry.enteredText} ${lastNameEntry.enteredText} " +
                "under username: ${emailEntry.enteredText}")

        val newUser = User(email = emailEntry.enteredText,
                firstName = firstNameEntry.enteredText,
                surname = lastNameEntry.enteredText,
                uuid = result["uid"].toString())

        val userRef = firebase.child("users").child(newUser.uuid)
        userRef.setValue(newUser)

        Toast.makeText(context, "Successfully registered! Please login.", Toast.LENGTH_SHORT).show()
        activity.finish()
    }

    private fun registrationFailed(firebaseError: FirebaseError) {
        debug("Failed to register user " +
                "${firstNameEntry.text.toString()} ${lastNameEntry.text.toString()} " +
                "under username: ${emailEntry.text.toString()}");
        debug("Firebase Error: ${firebaseError.code} - ${firebaseError.message}")

        showProgress(false, registrationForm, progressView)
        rootView?.makeSnack("Registration failed: ${firebaseError.message}", Snackbar.LENGTH_LONG)
    }
}
