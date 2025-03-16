package dev.lisek.meetly.ui.forgotPassword

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class PasswordResetModel:ViewModel() {
    fun resetPassword(email: String, callback: (String) -> Unit) {
        // Send password reset link
        var message = ""
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener { message = "Success" }
            .addOnFailureListener { task -> message = task.message.toString() }
            .addOnCompleteListener {
                callback(message)
            }
    }


}