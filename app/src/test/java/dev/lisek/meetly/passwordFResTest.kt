package dev.lisek.meetly

import dev.lisek.meetly.ui.forgotPassword.PasswordResetModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class PasswordResetModelTest {

    private lateinit var passwordResetModel: PasswordResetModel
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Before
    fun setUp() {
        passwordResetModel = PasswordResetModel()
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
    }

    @Test
    fun `resetPassword should return Success when email is valid`() {
        // Mock a successful task
        val successTask: Task<Void> = Tasks.forResult(null)
        `when`(mockFirebaseAuth.sendPasswordResetEmail("test@example.com")).thenReturn(successTask)

        var result = ""
        passwordResetModel.resetPassword("test@example.com") { message ->
            result = message
        }

        // Assert the expected outcome
        assertEquals("Success", result)
    }

    @Test
    fun `resetPassword should return error message when Firebase fails`() {
        // Simulate an exception from Firebase
        val exception = FirebaseAuthException("ERROR_INVALID_EMAIL", "Invalid email format")
        val failedTask: Task<Void> = Tasks.forException(exception)

        `when`(mockFirebaseAuth.sendPasswordResetEmail("invalid-email")).thenReturn(failedTask)

        var result = ""
        passwordResetModel.resetPassword("invalid-email") { message ->
            result = message
        }

        // Assert that the callback receives the correct error message
        assertEquals("Invalid email format", result)
    }
}
