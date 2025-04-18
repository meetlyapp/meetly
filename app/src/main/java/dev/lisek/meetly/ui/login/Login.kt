package dev.lisek.meetly.ui.login

import android.annotation.SuppressLint
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.lisek.meetly.R
import dev.lisek.meetly.backend.auth.Auth
import dev.lisek.meetly.backend.correctDate
import dev.lisek.meetly.ui.auth.googleAuth.GoogleAuth
import dev.lisek.meetly.ui.theme.DarkOrange
import dev.lisek.meetly.ui.theme.scriptFamily
import java.text.SimpleDateFormat
import kotlin.text.Regex

/**
 * Login screen.
 * 
 * @param [auth] authentication controller.
 * @param [pad] padding values.
 */
@SuppressLint("SimpleDateFormat")
@Composable
fun Login(auth: Auth, pad: PaddingValues = PaddingValues(0.dp), navController: NavController) {
    val context = LocalContext.current

    var isSignedIn by remember { mutableStateOf(false) }

    var hidePassword by remember { mutableStateOf(true) }
    var canRegister by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var nameValid by remember { mutableStateOf(true) }
    var surnameValid by remember { mutableStateOf(true) }
    var dateValid by remember { mutableStateOf(true) }
    var emailValid by remember { mutableStateOf(true) }
    var loginValid by remember { mutableStateOf(true) }
    var passwordValid by remember { mutableStateOf(true) }

    var emailTaken by remember { mutableStateOf(false) }
    var loginTaken by remember { mutableStateOf(false) }
    fun registrationForm(
        all: Boolean = false,
        checkName: Boolean = false,
        checkSurname: Boolean = false,
        checkDate: Boolean = false,
        checkEmail: Boolean = false,
        checkLogin: Boolean = false,
        checkPassword: Boolean = false,
    ): Boolean {
        if (all || checkName)
            nameValid = !name.isEmpty()
        if (all || checkSurname)
            surnameValid = !surname.isEmpty()
        if (all || checkDate)
            dateValid = correctDate(dob).second
        if (all || checkEmail)
            emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (all || checkLogin)
            loginValid = Regex("\\w+").matches(login)
        if (all || checkPassword)
            passwordValid = Regex(
                "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}"
            ).matches(password)
        return nameValid && surnameValid && dateValid &&
               emailValid && loginValid && passwordValid
    }

    Column(modifier = Modifier
        .padding(pad)
        .padding(16.dp)
        .fillMaxSize()
        .verticalScroll(ScrollState(0)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("meetly", fontFamily = scriptFamily, fontSize = 48.sp, color = DarkOrange)
        Text("find friends. make memories.", fontFamily = scriptFamily)
        Spacer(Modifier.height(8.dp))

        if (isSignedIn) {
            Column {
                OutlinedTextField(
                    name, label = { Text("First name") },
                    onValueChange = {
                        name = it
                        nameValid = !name.isEmpty()
                    },
                    shape = RoundedCornerShape(50),
                    isError = !nameValid
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    surname, label = { Text("Last name") },
                    onValueChange = {
                        surname = it
                        surnameValid = !surname.isEmpty()
                    },
                    shape = RoundedCornerShape(50),
                    isError = !surnameValid
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    dob, label = { Text("Date of birth") },
                    placeholder = { Text("DD/MM/YYYY") },
                    onValueChange = {
                        val ret = correctDate(it)
                        dob = ret.first
                        dateValid = ret.second
                    },
                    shape = RoundedCornerShape(50),
                    isError = !dateValid,
                    trailingIcon = {
                        Icon(Icons.Rounded.DateRange, contentDescription = "Select date")
                    }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    email, label = { Text("E-mail") },
                    onValueChange = {
                        email = it
                        registrationForm(checkEmail = true)
                        auth.db.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                emailTaken = !querySnapshot.isEmpty
                            }
                    },
                    shape = RoundedCornerShape(50),
                    isError = !emailValid || emailTaken,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        if (emailTaken)
                            Icon(Icons.Rounded.Warning, contentDescription = "Email taken")
                    }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            login, label = { Text("Username") },
            onValueChange = {
                login = it
                registrationForm(checkLogin = true)
                auth.db.collection("users")
                    .whereEqualTo("login", login)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        loginTaken = !querySnapshot.isEmpty
                    }
            },
            shape = RoundedCornerShape(50),
            isError = isSignedIn && (!loginValid || loginTaken),
            trailingIcon = {
                val icon = if (isSignedIn) {
                    Icons.Rounded.Warning
                } else {
                    Icons.Rounded.Check
                }
                if (loginTaken)
                    Icon(icon, contentDescription = "Login taken")
            }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            password, label = { Text("Password") },
            onValueChange = {
                password = it
                registrationForm(checkPassword = true)
            },
            shape = RoundedCornerShape(50),
            isError = isSignedIn && !passwordValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (hidePassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                val icon = if (hidePassword)
                    R.drawable.round_visibility
                else
                    R.drawable.round_visibility_off
                IconButton(onClick = { hidePassword = !hidePassword }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = (if (hidePassword) "Show" else "Hide") + "password"
                    )
                }
            }
        )
        Spacer(Modifier.height(8.dp))

        if (isSignedIn) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (-8).dp)
            ) {
                Checkbox(canRegister, { canRegister = it })
                Text("I agree to meetly's ", fontSize = 12.sp)
                Text("Terms and Conditions", fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {

                    }
                )
            }
        }
        Button(enabled = !isSignedIn || canRegister, onClick = {
            if (isSignedIn) {
                if (registrationForm(all = true)) {
                    auth.createAccount(
                        name, surname, login, email, password,
                        SimpleDateFormat("dd/MM/yyyy").parse(dob)!!
                    ) {
                        Toast.makeText(context, "Error creating an account", Toast.LENGTH_SHORT)
                            .show()
                    }
                    return@Button
                }
                if (!nameValid)
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                if (!surnameValid)
                    Toast.makeText(context, "Surname cannot be empty", Toast.LENGTH_SHORT).show()
                if (!dateValid)
                    Toast.makeText(context, "Provided date is invalid", Toast.LENGTH_SHORT).show()
                if (!emailValid)
                    Toast.makeText(context, "Provided email is invalid", Toast.LENGTH_SHORT).show()
                if (!loginValid)
                    Toast.makeText(context, "Username $login is already taken!", Toast.LENGTH_SHORT).show()
                if (!passwordValid)
                    Toast.makeText(context,
                        "Password must be at least 8 characters long and contain small and big letters, a digit and a special sign",
                        Toast.LENGTH_SHORT
                    ).show()
            } else if (!login.isEmpty() && !password.isEmpty()) {
                auth.signIn(context, login, password)
            }
            auth.checkAuth()
        }) { Text("Sign " + if (isSignedIn) "up" else "in") }

        if (!isSignedIn) {
            Text("or...")
            GoogleAuth(context,isSignedIn,navController)

            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                /* TODO("Facebook login") */
            }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook login",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text("Login with Facebook")
            }
        }
        Row(modifier = Modifier.padding(all = 16.dp)) {
            Text((if (isSignedIn) "Already" else "Don't") + " have an account? ")
            Text("Sign " + if (isSignedIn) "in" else "up",
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    isSignedIn = !isSignedIn
                }

            )
            Spacer(Modifier.height(8.dp))


        }
        Row (modifier = Modifier.padding(all = 16.dp))  {
            Text("Forgot password?",
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("resetScreen")
                }
            )
        }

    }
}
