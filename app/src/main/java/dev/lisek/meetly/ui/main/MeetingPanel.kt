package dev.lisek.meetly.ui.main

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.data.entity.Category
import dev.lisek.meetly.backend.data.entity.MeetingEntity
import dev.lisek.meetly.ui.Navigation
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.math.min

private var formatter = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault())

/**
 * Uploads an image to Firebase Storage and returns the download URL.
 *
 * @param [uri] URI of the image to upload.
 * @param [id] ID of the post.
 * @param [onSuccess] callback function to be called when the upload is successful.
 * @param [onFailure] callback function to be called when the upload fails.
 */
fun uploadImageToFirebase(uri: Uri, id: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = Firebase.storage.reference.child("posts/$id/photo.jpeg")
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

/**
 * Enum for visibility options.
 * 
 * @property [text] Text representation of the visibility option.
 */
enum class Visibility(val text: String) {
    PUBLIC("Public"),
    FRIENDS("Friends"),
    PRIVATE("Invite only")
}

/**
 * Converts milliseconds to a formatted date string.
 *
 * @param [millis] Milliseconds to convert.
 * @return Formatted date string.
 */
private fun convertMillisToDate(millis: Long): String {
    return formatter.format(Date(millis))
}

/**
 * Modal to select date and time.
 *
 * @param [onDateSelected] callback function to be called when the date is selected.
 * @param [onDismiss] callback function to be called when the modal is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentZone = ZoneId.systemDefault() // Get system's default time zone
    val currentDateTime = LocalDateTime.now(currentZone) // Get current local date and time

    // Initialize the DatePickerState with current date
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentDateTime.atZone(currentZone).toInstant().toEpochMilli())

    // Initialize the TimePickerState with current time
    val timePickerState = rememberTimePickerState(
        initialHour = currentDateTime.hour,
        initialMinute = currentDateTime.minute,
        is24Hour = false
    )

    var inTimePicker by remember { mutableStateOf(false) }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (inTimePicker) {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        // Combine selected date and time
                        val localDateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(selectedDate),
                            currentZone
                        )
                        val updatedTime = localDateTime.withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)

                        // Convert back to milliseconds
                        val localMillis = updatedTime.atZone(currentZone).toInstant().toEpochMilli()
                        onDateSelected(localMillis)
                    } else {
                        onDateSelected(null)
                    }
                    onDismiss()
                } else {
                    inTimePicker = true
                }
            }) {
                Text(if (inTimePicker) "OK" else "Next")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (inTimePicker) {
                    inTimePicker = false
                } else {
                    onDismiss()
                }
            }) {
                Text(if (inTimePicker) "Back" else "Cancel")
            }
        }
    ) {
        if (inTimePicker) {
            Column {
                Text(
                    "Select time",
                    Modifier.padding(24.dp, 16.dp, 0.dp, 32.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .1.sp
                )
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    TimePicker(timePickerState)
                }
            }
        } else {
            DatePicker(datePickerState)
        }
    }
}

/**
 * Meeting panel.
 * 
 * @param [data] Meeting entity.
 * @param [create] True if creating a new meeting, false if the meeting already exists.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun MeetingPanel(
    data: MeetingEntity = MeetingEntity(),
    create: Boolean = true
) {
    val uid = Firebase.auth.currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    var title by remember { mutableStateOf(data.title ?: "New meeting") }
    var author by mutableStateOf(data.author)
    var desc by remember { mutableStateOf(data.description ?: "Description") }
    var categoryMenu by remember { mutableStateOf(false) }
    var category = remember { mutableStateListOf<Category>(*data.category.toTypedArray()) }
    var mapModal by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(data.address) }
    var location = remember { mutableStateMapOf(*data.location.toList().toTypedArray()) }
    var dateModal by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(formatter.format(data.date)) }
    var people by remember { mutableIntStateOf(data.maxParticipants) }
    var participants = remember { mutableStateListOf<String>(*data.participants.toTypedArray()) }
    var visibilityMenu by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(data.visible) }
    var edit by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }

    Column(Modifier
        .verticalScroll(ScrollState(0))
        .imePadding()
    ) {
        Box {
            AsyncImage(
                model = data.image,
                contentDescription = "Meeting image",
                placeholder = painterResource(R.drawable.landscape),
                error = painterResource(R.drawable.landscape),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
            )
            AsyncImage(
                model = imageUri,
                contentDescription = "Picked Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clickable(create || edit) {
                        launcher.launch("image/*")
                    }
            )
            Box(Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
            )
            if (data.creator == uid) {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 44.dp), Arrangement.End) {
                    IconButton({ delete = true }) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            }
        }
        Column(
            Modifier.padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                BasicTextField(
                    title,
                    { title = it },
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (create || edit)
                                TextFieldDefaults.colors().focusedContainerColor
                            else
                                Color.Transparent
                        ).padding(8.dp),
                    enabled = create || edit,
                    textStyle = TextStyle(
                        MaterialTheme.colorScheme.onBackground,
                        24.sp,
                        FontWeight.Bold
                    )
                )
                LazyRow {
                    items(category) {
                        Row(
                            Modifier
                                .width(48.dp)
                                .height(28.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(it.color),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ) {
                            Text(it.emoji, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
            Row(Modifier
                .fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Row(Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        Navigation.navigate("profile/${data.creator}")
                    }
                    .padding(8.dp, 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(author.image, null, Modifier
                        .size(32.dp)
                        .clip(CircleShape))
                    Text(
                        "${author.name} ${author.surname}",
                        Modifier.padding(8.dp)
                    )
                    Text(
                        "(${author.login})",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                        fontSize = 12.sp
                    )
                }
                Row(Modifier.padding(8.dp)) {
                    Icon(Icons.Default.Person, null)
                    Text("${participants.size}${if (people == 0) "" else "/$people"}")
                }
            }
            BasicTextField(
                desc,
                { desc = it },
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (create || edit)
                            TextFieldDefaults.colors().focusedContainerColor
                        else
                            Color.Transparent
                    ).padding(8.dp),
                enabled = create || edit,
                textStyle = TextStyle(MaterialTheme.colorScheme.onBackground, 16.sp),
            )
            if (create || edit) {
                Spacer(Modifier.height(32.dp))
                Box {
                    Box {
                        TextField(
                            category.joinToString("\n") { it.string },
                            { },
                            Modifier.fillMaxWidth(),
                            label = { Text("Categories") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                        )
                        Box(Modifier
                            .matchParentSize()
                            .clickable {
                                categoryMenu = true
                            })
                    }
                    DropdownMenu(categoryMenu, { categoryMenu = false }) {
                        for (item in Category.entries) {
                            DropdownMenuItem({ Text(item.string) }, {
                                if (item in category) {
                                    category.remove(item)
                                } else if (category.size < 3) {
                                    category.add(item)
                                }
                            })
                        }
                    }
                }
                Box {
                    TextField(
                        address,
                        { },
                        Modifier.fillMaxWidth(),
                        label = { Text("Location") },
                        shape = RectangleShape,
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Rounded.LocationOn, contentDescription = "Select location")
                        }
                    )
                    Box(Modifier
                        .matchParentSize()
                        .clickable { mapModal = true } // { nav.navigate("map") }
                    )
                }
                Box {
                    TextField(
                        date, {},
                        Modifier.fillMaxWidth(),
                        label = { Text("Date") },
                        shape = RectangleShape,
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Rounded.DateRange, contentDescription = "Select date")
                        }
                    )
                    Box(Modifier
                        .matchParentSize()
                        .clickable { dateModal = true }
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        if (people == 0) "" else people.toString(), {
                            people = if (it == "") 0 else it
                                .substring(0, min(it.length, 5))
                                .toInt()
                        },
                        Modifier.fillMaxWidth(.5f),
                        label = { Text("Max people") },
                        placeholder = { Text("Unlimited") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(56.dp)
                            .background(TextFieldDefaults.colors().unfocusedLabelColor)
                    )
                    Box {
                        Box {
                            TextField(
                                visibility.text,
                                { },
                                Modifier.fillMaxWidth(),
                                label = { Text("Visibility") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                            )
                            Box(Modifier
                                .matchParentSize()
                                .clickable {
                                    visibilityMenu = true
                                })
                        }
                        DropdownMenu(visibilityMenu, { visibilityMenu = false }) {
                            for (item in Visibility.entries) {
                                DropdownMenuItem({ Text(item.text) }, {
                                    visibility = item
                                    visibilityMenu = false
                                })
                            }
                        }
                    }
                }
                Row(Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(50)),
                    Arrangement.Center
                ) {
                    if (!create) {
                        Button(
                            { edit = false },
                            Modifier.fillMaxWidth(.5f),
                            shape = RectangleShape
                        ) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(1.dp))
                    }
                    Button(
                        {
                            val meeting = mapOf(
                                "title" to title,
                                "creator" to uid,
                                "description" to desc,
                                "date" to formatter.parse(date),
                                "location" to mapOf(
                                    "latitude" to location["latitude"],
                                    "longitude" to location["longitude"]
                                ),
                                "address" to address,
                                "categories" to category,
                                "maxParticipants" to people,
                                "ageRange" to mapOf(
                                    "from" to null,
                                    "to" to null
                                ),
                                "visibility" to visibility.name
                            )
                            val doc = if (create) {
                                db.collection("posts").document()
                            } else {
                                db.collection("posts").document(data.id!!)
                            }
                            doc.set(meeting).addOnSuccessListener {
                                Log.d("CreateMeeting", "CreateMeeting:success")
                                if (create)
                                    Navigation.nav?.popBackStack()
                                else
                                    edit = false
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
                        }, Modifier.fillMaxWidth(),
                        shape = RectangleShape
                    ) { Text(if (create) "Create" else "Save") }
                }
            } else {
                var text = ""
                var icon = Icons.Default.Warning
                var onClick = {}

                if (uid == data.creator) {
                    text = "Edit"
                    icon = Icons.Default.Edit
                    onClick = { edit = true }
                } else if (uid in participants) {
                    text = "Unregister"
                    icon = Icons.Default.Check
                    onClick = {
                        db.collection("posts")
                            .document(data.id!!)
                            .update("participants", FieldValue.arrayRemove(uid))
                            .addOnSuccessListener {
                                participants.remove(uid)
                            }
                    }
                } else {
                    text = "Register"
                    icon = Icons.AutoMirrored.Default.Send
                    onClick = {
                    db.collection("posts")
                        .document(data.id!!)
                        .update("participants", FieldValue.arrayUnion(uid))
                        .addOnSuccessListener {
                            participants.add(uid)
                        }
                    }
                }
                Button(onClick, Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp)) {
                    Text(text)
                    Spacer(Modifier.width(8.dp))
                    Icon(icon, null)
                }
                Row {
                    Icon(Icons.Default.Place, null)
                    Spacer(Modifier.height(8.dp))
                    Text(address)
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    Icon(Icons.Default.DateRange, null)
                    Spacer(Modifier.height(8.dp))
                    Text(date)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
    if (dateModal) {
        DatePickerModal({ date = convertMillisToDate(it!!) }) { dateModal = false }
    }
    if (mapModal) {
        Dialog(
            { mapModal = false }
        ) {
            Map { loc, addr ->
                location["latitude"] = loc.latitude
                location["longitude"] = loc.longitude
                address = addr
                mapModal = false
            }
        }
    }
    if (delete) {
        AlertDialog(
            { delete = false },
            { Button({
                db.collection("posts")
                    .document(data.id!!)
                    .delete()
                    .addOnSuccessListener {
                        Log.i("Meeting", "Meeting ${data.id} deleted successfully")
                        delete = false
                        Navigation.nav?.popBackStack()
                    }
            }) { Text("Yes") } },
            Modifier,
            { Button({ delete = false }) { Text("No") } },
            null,
            { Text("Delete meeting") },
            { Text("Do you want to delete \"$title?\"")})
    }
}
