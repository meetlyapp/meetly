package dev.lisek.meetly.backend.auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

/**
 * Collection of authentication methods.
 * 
 * @constructor Create empty Auth
 * @param [nav] navigation controller.
 */
class Auth(val nav: NavController) : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val TAG = "Auth"

    /**
     * Checks if the user is logged in.
     * 
     * @return true if the user is logged in, false otherwise
     */
    fun isLogged(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Checks if the user is logged in and, if yes, navigates to the main screen.
     */
    fun checkAuth() {
        if (isLogged()) {
            nav.navigate("app") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    /**
     * Creates a user account.
     * 
     * @param [name] user's name.
     * @param [surname] user's surname.
     * @param [login] user's login.
     * @param [email] user's email.
     * @param [password] user's password.
     * @param [dob] user's date of birth.
     * @param [onFailure] callback on failure.
     */
    fun createAccount(
        name: String,
        surname: String,
        login: String,
        email: String,
        password: String,
        dob: Date,
        onFailure: () -> Unit
    ) {
        auth.signOut()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val data = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "login" to login,
                        "email" to email,
                        "dob" to Timestamp(dob),
                        "location" to hashMapOf("latitude" to 0, "longitude" to 0),
                        "meetings" to emptyList<Any>()
                    )
                    db.collection("users")
                        .document(uid)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d(TAG, "createUserWithEmail:success")
                            checkAuth()
                        }

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onFailure()
                }
            }
    }

    /**
     * Signs in the user.
     * 
     * @param [context] activity context.
     * @param [login] user's login.
     * @param [password] user's password.
     */
    fun signIn(context: Context, login: String, password: String) {
        auth.signOut()
        if (Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        checkAuth()
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(context, "Invalid e-mail or password", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            db.collection("users")
                .whereEqualTo("login", login)
                .get()
                .addOnSuccessListener { user ->
                    if (user.documents.isEmpty()) {
                        Log.w(TAG, "No user found with username: $login")
                        Toast.makeText(context, "Username $login not found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    val email = user.documents[0].getString("email") ?: "!"
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { task ->
                            Log.d(TAG, "signInWithEmail:success")
                            checkAuth()
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "signInWithEmail:failure", exception)
                            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error retrieving users")
                    Toast.makeText(context, "Error retrieving users", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
