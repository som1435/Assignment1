package com.example.profilecompose

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.profilecompose.ui.theme.ProfileComposeTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.regex.Pattern


class MainActivity : ComponentActivity() {

    private val Context.profileStore: DataStore<Profile> by dataStore(
        fileName = "profile.pb",
        serializer = ProfileSerializer
    )

    private lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileRepository = ProfileRepository(profileStore)
        setContent {
            ProfileComposeTheme {
                ProfileScreen(profileRepository)
            }
        }
    }
}

@Composable
fun ProfileScreen(profileRepository: ProfileRepository) {
    val name = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf(false) }
    val phoneError = remember { mutableStateOf(false) }
    val nameError = remember { mutableStateOf(false) }

    val enableButtonState = remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

//    scope.launch {
//        val res: Profile = profileRepository.getData()
//        withContext(Dispatchers.Main) {
//            name.value = res.name
//            phone.value = res.phone
//            email.value = res.email
//        }
//    }

    LaunchedEffect(context) {
        profileRepository.profileFlow.collect {
            name.value = it.name
            phone.value = it.phone
            email.value = it.email
        }
    }

//    scope.launch {
//        profileRepository.profileFlow.collect {
//            withContext(Dispatchers.Main) {
//                name.value = it.name
//                phone.value = it.phone
//                email.value = it.email
//            }
//        }
//    }

//    enableButtonState.value = enableButton(
//        "" + name.value.toString(),
//        "" + phone.value.toString(),
//        "" + email.value.toString()
//    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(.2f),
            backgroundColor = Color(0xFF6200EE)
        ) { }
        Spacer(modifier = Modifier.height(20.dp))
//        Text(text = "Hello")
//        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = name.value,
            onValueChange = {
                name.value = it
                nameError.value = !isValidName(it.trim())
                enableButtonState.value = isValidInput(name.value, phone.value, email.value)
            },
            isError = nameError.value,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                unfocusedBorderColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = phone.value,
            onValueChange = {
                phone.value = it
                phoneError.value = !isValidPhone(it.trim())
                enableButtonState.value = isValidInput(name.value, phone.value, email.value)
            },
            isError = phoneError.value,
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                unfocusedBorderColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                emailError.value = !isValidEmail(it.trim())
                enableButtonState.value = isValidInput(name.value, phone.value, email.value)
            },
            isError = emailError.value,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                unfocusedBorderColor = Color(0xFF6200EE),
                unfocusedLabelColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Change Password",
            modifier = Modifier.padding(all = 8.dp),
            color = Color(0xFF6200EE),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    profileRepository.saveDetails(name.value, phone.value, email.value)
                }
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF6200EE),
                disabledBackgroundColor = Color.Gray
            ),
            enabled = enableButtonState.value
        ) {
            Text("Save Details", color = Color.White)
        }
    }
}

fun enableButton(name: String, phone: String, email: String) =
    name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()

fun isValidInput(name: String?, phone: String?, emailStr: String?): Boolean =
    isValidName(name) && isValidPhone(phone) && isValidEmail(emailStr)

fun isValidEmail(emailStr: String?) =
    Pattern
        .compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
        ).matcher(emailStr).find()

fun isValidName(name: String?) =
    Pattern
        .compile(
            "^([a-zA-Z]{2,}\\s[a-zA-Z]{1,}'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]{1,})?)",
            Pattern.CASE_INSENSITIVE
        ).matcher(name).find()

fun isValidPhone(phone: String?) =
    Pattern
        .compile(
            "((\\+*)((0[ -]*)*|((91 )*))((\\d{12})+|(\\d{10})+))|\\d{5}([- ]*)\\d{6}",
            Pattern.CASE_INSENSITIVE
        ).matcher(phone).find()


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ProfileScreen()
//}