package com.krithel.techmessenger.login.model;

/**
 * Created by Krithel on 03-Mar-16.
 */
public class LoginValidation {

    private boolean isValid = true;
    private int errorResId;
    private InvalidField invalidField;

    public LoginValidation() {
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getErrorResId() {
        return errorResId;
    }

    public void setErrorResId(int errorResId) {
        this.errorResId = errorResId;
    }

    public InvalidField getInvalidField() {
        return invalidField;
    }

    public void setInvalidField(InvalidField invalidField) {
        this.invalidField = invalidField;
    }

    public enum InvalidField {
        EMAIL,
        PASSWORD
    }
}
