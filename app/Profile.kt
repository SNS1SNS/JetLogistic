package com.example.jet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jet.CalendarWithMenuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarWithMenuScreen()
        }
    }
}


@Composable
fun ProfileScreen(currentUser: User?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Профиль сотрудника", fontWeight = FontWeight.Bold, color = Color(0xFF267AFC)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Menu, tint = Color(0xFF267AFC), contentDescription = "Back")
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
            // Avatar
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
            if (currentUser != null) {
                ProfileField(label = "Фамилия", value = currentUser.lastName ?: "")
                ProfileField(label = "Имя", value = currentUser.firstName ?: "")
                ProfileField(label = "Электронная почта", value = currentUser.email ?: "")
                ProfileField(label = "Роли", value = currentUser.position_name ?: "")
                ProfileField(label = "Баланс", value = currentUser.balance?.toString() ?: "0")
                ProfileField(label = "Статус", value = if (currentUser.isActive) "true" else "false")
            } else {
                Text("Данные загружаются...", fontSize = 16.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(onClick = { /* Save profile changes */ }) {
                Text("Сохранить", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF267AFC))
        OutlinedTextField(
            value = value,
            onValueChange = { /* Update state here */ },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
