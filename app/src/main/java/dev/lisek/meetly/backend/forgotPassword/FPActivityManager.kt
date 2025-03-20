package dev.lisek.meetly.backend.forgotPassword

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FPActivityManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun sendResetLink(email: String, callback: (String) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email) // Query Firestore for the email
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
