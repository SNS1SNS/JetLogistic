package com.example.jet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jet.connection.RetrofitClient
import com.example.jet.model.ApiService
import com.example.jet.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                currentUser = null, // Pass null initially
                onBack = { finish() }
            )
        }

        // Fetch user data after the screen is set up
        CoroutineScope(Dispatchers.Main).launch {
            val user = fetchUserData()
            setContent {
                ProfileScreen(
                    currentUser = user,
                    onBack = { finish() }
                )
            }
        }
    }

    suspend fun fetchUserData(): User? {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)

        return withContext(Dispatchers.IO) { // Run network operation on IO dispatcher
            try {
                if (authToken != null) {
                    val apiService = RetrofitClient.instance.create(ApiService::class.java)
                    val response = apiService.getCurrentUser("Bearer $authToken").execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        null // Handle unsuccessful response
                    }
                } else {
                    null // Handle missing authToken
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null // Handle exceptions
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(currentUser: User?, onBack: () -> Unit) {
    // Local state for editable fields
    var firstName by remember { mutableStateOf(currentUser?.first_name ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.last_name ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var balance by remember { mutableStateOf(currentUser?.balance?.toString() ?: "") }
    var positionName by remember { mutableStateOf(currentUser?.position_name ?: "") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Профиль сотрудника",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF267AFC)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Back",
                            tint = Color(0xFF267AFC)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Editable Fields
            ProfileField("Имя", firstName) { firstName = it }
            ProfileField("Фамилия", lastName) { lastName = it }
            ProfileField("Электронная почта", email) { email = it }
            ProfileField("Баланс", balance) { balance = it }
            ProfileField("Роль", positionName) { positionName = it }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    println("Saving: $firstName, $lastName, $email, $balance, $positionName")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF267AFC),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp), // Rounded corners
                elevation = ButtonDefaults.elevatedButtonElevation(8.dp) // Elevation for a shadow effect
            ) {
                Text("Сохранить", fontSize = 18.sp)
            }

        }
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF267AFC)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

