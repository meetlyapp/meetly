import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import dev.lisek.meetly.backend.auth.forgotPassword.PasswordResetModel
@Composable
fun ResetPasswordScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isSuccess by remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current
    val sender = PasswordResetModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Reset Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.text.isNotEmpty()) {
                    sender.sendResetLink(email.text) { result ->
                        isSuccess = result == "Success"
                        Handler(Looper.getMainLooper()).post {
                            val message = if (isSuccess == true) "Reset link sent!" else "Failed to send reset link!"
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.text.isNotEmpty()
        ) {
            Text("Send Reset Link")
        }
    }
}
