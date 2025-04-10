package dev.lisek.meetly.backend.auth.forgotPassword

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/**
 * \[FPActivityManager\] is responsible for managing user activities related to
 * forgotten password functionality. It provides a method to send a password
 * reset link to a user's email address, after verifying its existence in the
 * Firestore database.
 *
 * @constructor Creates an instance of \[FPActivityManager\].
 */
class FPActivityManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Sends a password reset link to the specified email address if it exists in the Firestore database.
     *
     * @param email The email address to which the password reset link will be sent.
     * @param callback A callback function that will be invoked with the result of the operation.
     * The callback will receive a string message indicating success or failure.
     */
    fun sendResetLink(email: String, callback: (String) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Email exists, proceed with password reset
                    auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener { callback("Success") }
                        .addOnFailureListener { callback("Failure: ${it.message}") }
                } else {
                    callback("Failure: Email not registered")
                }
            }
            .addOnFailureListener { callback("Failure: ${it.message}") }
    }
}
