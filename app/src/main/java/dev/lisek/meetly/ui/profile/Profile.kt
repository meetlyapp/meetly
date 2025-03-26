package dev.lisek.meetly.ui.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.storage
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.data.FetchData.db
import dev.lisek.meetly.backend.profile.ProfileEntity
import dev.lisek.meetly.backend.profile.ProfileWrapper

object Profile {

    fun uploadImageToFirebase(uri: Uri, uid: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = Firebase.storage.reference.child("users/$uid/profile.jpeg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString()) // Get the URL of the uploaded image
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    @Composable
    fun getEntity(user: String): ProfileEntity? {
        val profile = remember { ProfileWrapper() }

        LaunchedEffect(user) {
            profile.profileEntity(user)
        }

        return profile.data
    }

    val local: ProfileEntity
        @Composable get() = getEntity(Firebase.auth.uid!!) ?: ProfileEntity()

    @Composable
    fun FromUID(user: String) {
        FromEntity(getEntity(user))
    }

    @Composable
    private fun FromEntity(data: ProfileEntity?) {
        data?.let {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                imageUri = uri
            }

            val loc = local
            var friends = remember { mutableStateListOf<String>(*it.friends.toTypedArray()) }
            var incomingFriends = remember { mutableStateListOf<String>(*it.incomingFriends.toTypedArray()) }
            var outgoingFriends = remember { mutableStateListOf<String>(*it.outgoingFriends.toTypedArray()) }
            var edit by remember { mutableStateOf(false) }

            AsyncImage(
                model = data.image,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.64f),
                placeholder = painterResource(R.drawable.profile),
                error = painterResource(R.drawable.profile),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = imageUri,
                contentDescription = "Picked picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.64f),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.verticalScroll(ScrollState(0))) {
                Box(Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.64f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ).clickable(edit) {
                        launcher.launch("image/*")
                    }
                )
                Column(Modifier.background(MaterialTheme.colorScheme.background)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "${it.name} ${it.surname}, ${it.age}",
                            fontSize = 24.sp,
                            lineHeight = 24.sp
                        )
                        Text("@${it.login}", Modifier.padding(2.dp, 0.dp), fontSize = 12.sp)
                    }
                    var text = ""
                    var icon = Icons.Default.Warning
                    var onClick = {}

                    if (it.uid in friends || it.uid in loc.friends) {
                        text = "Friends"
                        icon = Icons.Default.Favorite
                        onClick = {
                            loc.doc.update(
                                "friends",
                                FieldValue.arrayRemove(it.uid)
                            ).addOnSuccessListener {
                                friends.remove(data.uid)
                            }
                            it.doc.update("friends", FieldValue.arrayRemove(loc.uid))
                        }
                    } else if (it.uid in outgoingFriends || it.uid in loc.outgoingFriends) {
                        text = "Invited"
                        icon = Icons.AutoMirrored.Default.Send
                        onClick = {
                            loc.doc.update(
                                "outgoingFriends",
                                FieldValue.arrayRemove(it.uid)
                            ).addOnSuccessListener {
                                outgoingFriends.remove(data.uid)
                            }
                            it.doc.update("incomingFriends", FieldValue.arrayRemove(loc.uid))
                        }
                    } else if (it.uid in incomingFriends || it.uid in loc.incomingFriends) {
                        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                            Text("${it.name} sent you a friend request.")
                        }
                        text = "Accept"
                        icon = Icons.Default.Check
                        onClick = {
                            loc.doc.update(
                                "incomingFriends",
                                FieldValue.arrayRemove(it.uid)
                            )
                            loc.doc.update("friends", FieldValue.arrayUnion(it.uid))
                                .addOnSuccessListener {
                                    incomingFriends.remove(data.uid)
                                    friends.add(data.uid)
                                }
                            it.doc.update("outgoingFriends", FieldValue.arrayRemove(loc.uid))
                            it.doc.update("friends", FieldValue.arrayUnion(loc.uid))
                        }
                    } else {
                        text = "Add friend"
                        icon = Icons.Default.Person
                        onClick = {
                            loc.doc.update("outgoingFriends", FieldValue.arrayUnion(it.uid))
                                .addOnSuccessListener {
                                    outgoingFriends.add(data.uid)
                                }
                            it.doc.update("incomingFriends", FieldValue.arrayUnion(loc.uid))
                        }
                    }
                    Row(
                        Modifier
                            .padding(8.dp)
                            .height(40.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                    ) {
                        if (data.uid == local.uid && edit) {
                            Button({
                                val doc = db.collection("users").document(data.uid)
                                doc.set(data).addOnSuccessListener {
                                    Log.d("CreateMeeting", "CreateMeeting:success")
                                }
                                imageUri?.let { uri ->
                                    uploadImageToFirebase(uri, doc.id,
                                        onSuccess = { downloadUrl ->
                                            Log.d("Upload", "Image uploaded: $downloadUrl")
                                        },
                                        onFailure = { exception ->
                                            Log.e("Upload", "Upload failed", exception)
                                        }
                                    )
                                }
                                edit = false
                            }, Modifier.fillMaxWidth()) {
                                Text("Save")
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.Check, contentDescription = "Save changes")
                            }
                        } else if (data.uid == local.uid) {
                            Button({ edit = true }, Modifier.fillMaxWidth()) {
                                Text("Edit")
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                            }
                        } else {
                            Button(onClick, Modifier.fillMaxWidth(.5f), shape = RectangleShape) {
                                Icon(icon, contentDescription = "Add friend")
                                Spacer(Modifier.width(8.dp))
                                Text(text)
                            }
                            Spacer(Modifier.width(1.dp))
                            Button({}, Modifier.fillMaxWidth(), shape = RectangleShape) {
                                Text("Message")
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.MailOutline, contentDescription = "Message")
                            }
                        }
                    }
                    Column(Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(
                            "About ${it.name}",
                            Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (!it.bio.isEmpty())
                                it.bio.replace("\\n", "\n")
                            else
                                "${it.name} did not provide a description yet."
                        )
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}
