package dev.lisek.meetly.backend.auth.forgotPassword

import androidx.lifecycle.ViewModel

/**
 * \[PasswordResetModel\] is a ViewModel that handles the logic for sending a password reset link
 * using the \[FPActivityManager\].
 *
 * @constructor Creates an instance of \[PasswordResetModel\].
 */
class PasswordResetModel : ViewModel() {
    private val fpActivityManager = FPActivityManager()

    /**
     * Sends a password reset link to the specified email address.
     *
     * @param email The email address to which the password reset link will be sent.
     * @param callback A callback function that will be invoked with the result of the operation.
     * The callback will receive a string message indicating success or failure.
     */
    fun sendResetLink(email: String, callback: (String) -> Unit) {
        fpActivityManager.sendResetLink(email) { message ->
            callback(message)
        }
    }
}