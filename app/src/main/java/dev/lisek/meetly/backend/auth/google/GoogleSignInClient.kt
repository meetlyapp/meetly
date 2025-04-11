package dev.lisek.meetly.backend.auth.google

import Location
import ProfileEntity
import android.content.Context
import android.util.Log
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


class GoogleSignInClient(
    private val context: Context,
    private var signInStatus: Boolean,

    ) {
    private val tag = "GoogleAuthClient: "

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun isSignedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            println(tag + "User is signed in")
            signInStatus = true
            return true
        }
        return false
    }

    suspend fun signIn(): Boolean {
        if (isSignedIn()) {
            return true
        }
        try {
            val result = buildCredentialRequest()
            return handleSingIn(result)


        } catch (e: Exception) {

            e.printStackTrace()
            if (e is CancellationException) throw e
            println(tag + "Failed to sign in with Google: " + e.message)
            return false
        }

    }

    private suspend fun handleSingIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()


                val user = authResult.user
                if (user != null) {
                    val userRef = firestore.collection("users").document(user.uid)

                    val snapshot = userRef.get().await()
                    if (!snapshot.exists()) {
                        val profile = ProfileEntity(
                            uid = user.uid,
                            name = tokenCredential.givenName ?: "",
                            surname = tokenCredential.familyName ?: "",
                            email = tokenCredential.id,
                            login = "",
                            bio = "",
                            dob = Timestamp.now(),
                            location = Location(0.0, 0.0),
                            friends = emptyList(),
                            incomingFriends = emptyList(),
                            outgoingFriends = emptyList()
                        )

                        userRef.set(profile).await()
                        Log.d(tag, "New user profile saved to Firestore.")
                    } else {
                        Log.d(tag, "User already exists in Firestore.")
                    }
                }

                println(tag + "name: ${tokenCredential.displayName}")
                println(tag + "email: ${tokenCredential.id}")
                println(tag + "image: ${tokenCredential.profilePictureUri}")
                return authResult.user != null

            } catch (e: GoogleIdTokenParsingException) {
                println(tag + "GoogleIdTokenParsingException: ${e.message}")
                return false
            }

        } else {
            println(tag + "credential is not GoogleIdTokenCredential")
            return false
        }

    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("735570026619-fm8q7nc7r260elir54mirkhuaubgp13n.apps.googleusercontent.com")
                    .setAutoSelectEnabled(true)
                    .build()
            )
            .build()
        return credentialManager.getCredential(
            request = request,
            context = context
        )


    }
}