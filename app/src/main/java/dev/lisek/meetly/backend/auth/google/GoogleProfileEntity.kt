import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

interface Locatable {
    val location: Location
}

data class ProfileEntity(
    val uid: String,
    val name: String,
    val surname: String,
    val email: String,
    val login: String = "",
    val bio: String = "",
    val dob: Timestamp = Timestamp.now(),
    override val location: Location = Location(),
    val friends: List<String> = emptyList(),
    val incomingFriends: List<String> = emptyList(),
    val outgoingFriends: List<String> = emptyList()
) : Locatable {

    fun validate(): Boolean {
        return uid.isNotBlank() &&
                name.isNotBlank() &&
                surname.isNotBlank() &&
                email.isNotBlank() &&
                login.isNotBlank() &&
                bio.isNotBlank() &&
                dob != Timestamp.now()
    }

    val age: Int
        get() {
            val dobDate = dob.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val now = LocalDate.now()
            return Period.between(dobDate, now).years
        }
}

