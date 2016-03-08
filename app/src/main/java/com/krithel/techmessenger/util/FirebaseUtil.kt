package com.krithel.techmessenger.util

import com.firebase.client.Firebase
import com.firebase.client.FirebaseError

/**
 * Created by Krithel on 08-Mar-16.
 */
fun Firebase.createUser(email: String, password: String,
                        success: (Map<String, Any>) -> Unit,
                        failure: (FirebaseError) -> Unit) {
    createUser(email, password, object : Firebase.ValueResultHandler<Map<String, Any>> {

        override fun onSuccess(result: Map<String, Any>) {
            success(result)
        }

        override fun onError(firebaseError: FirebaseError) {
            failure(firebaseError)
        }
    })
}