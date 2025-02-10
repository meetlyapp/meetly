package dev.lisek.meetly.backend

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.Date

class Auth(val nav: NavController) : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val TAG = "Auth"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
//        val currentUser = auth.currentUser
//        checkAuth()
    }

    fun isLogged(): Boolean {
        return auth.currentUser != null
    }

    fun checkAuth() {
        if (isLogged()) {
            nav.navigate("app") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

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
//                    auth.currentUser!!.updateProfile(userProfileChangeRequest {
//                        displayName = login
//                        photoUri = Uri.parse("https://beforeigosolutions.com/wp-content/uploads/2021/12/dummy-profile-pic-300x300-1.png")
//                    })
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
                    onFailure
                }
            }
    }

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