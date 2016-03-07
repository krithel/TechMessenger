package com.krithel.techmessenger.login.util;

import android.text.TextUtils;

import com.krithel.techmessenger.R;
import com.krithel.techmessenger.login.model.LoginValidation;

/**
 * Created by Krithel on 03-Mar-16.
 */
public class LoginUtils {

    public static LoginValidation validateEmailAndPasseword(String email, String password) {
        LoginValidation val = new LoginValidation();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            val.setErrorResId(R.string.error_invalid_password);
            val.setInvalidField(LoginValidation.InvalidField.PASSWORD);
            val.setValid(false);
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            val.setErrorResId(R.string.error_field_required);
            val.setInvalidField(LoginValidation.InvalidField.EMAIL);
            val.setValid(false);
        } else if (!isEmailValid(email)) {
            val.setErrorResId(R.string.error_invalid_email);
            val.setInvalidField(LoginValidation.InvalidField.EMAIL);
            val.setValid(false);
        }

        return val;
    }

    private static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private static boolean isEmailValid(String email) {
        return email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
    }
}
