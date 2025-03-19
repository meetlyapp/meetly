import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController

@Composable
fun ResetPasswordScreen( modifier: Modifier = Modifier, navController: NavHostController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isSuccess by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
                TODO("Implement password reset")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.text.isNotEmpty()
        ) {
            Text("Send Reset Link")
        }

        isSuccess?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (it) "Reset link sent! Check your email." else "Failed to send reset link.",
                color = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
