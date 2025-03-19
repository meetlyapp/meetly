package dev.lisek.meetly.backend.forgotPassword

import androidx.lifecycle.ViewModel

class PasswordResetModel:ViewModel() {
    private val fpActivityManager = FPActivityManager()

    fun sendResetLink(email:String, callback:(String)->Unit) {
        fpActivityManager.sendResetLink(email){ message ->
            callback(message)
        }
    }


}