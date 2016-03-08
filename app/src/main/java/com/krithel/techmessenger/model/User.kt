package com.krithel.techmessenger.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Created by Krithel on 03-Mar-16.
 */
data class User(
        @JsonProperty("email")
        var email: String?,
        @JsonProperty("uuid")
        var uuid: String?,
        @JsonProperty("firstName")
        var firstName: String?,
        @JsonProperty("surname")
        var surname: String?,
        @JsonProperty("conversations")
        var conversations: Map<String, String>? = HashMap<String, String>()) {

    val fullName: String
        @JsonIgnore
        get() = String.format("%s %s", firstName, surname)
}
