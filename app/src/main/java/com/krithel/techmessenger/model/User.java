package com.krithel.techmessenger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Created by Krithel on 03-Mar-16.
 */
public class User {

    private String email;
    private String uuid;
    private String firstName;
    private String surname;
    private Map<String, String> conversations;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Map<String, String> getConversations() {
        return conversations;
    }

    public void setConversations(Map<String, String> conversations) {
        this.conversations = conversations;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", firstName, surname);
    }
}
