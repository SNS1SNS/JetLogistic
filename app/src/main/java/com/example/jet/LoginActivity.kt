package com.example.jet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jet.connection.RetrofitClient
import com.example.jet.model.ApiService
import com.example.jet.model.LoginRequest
import com.example.jet.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun performLogin() {
        isLoading = true
        errorMessage = null
        successMessage = null

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val loginRequest = LoginRequest(email = login, password = password)

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    successMessage = "Успешный вход"

                    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("authToken", loginResponse?.token).apply()

                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    errorMessage = "Ошибка: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Сбой подключения: ${t.message}"
            }
        })
    }


    var isNavigated = false
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFFFFF)), // Светло-серый фон

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )

    {
        // Логотип
        Image(
            painter = painterResource(id = R.drawable.logo_vis),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1.5f)
                .padding(bottom = 32.dp)
        )

        // Заголовок
        Text(
            text = "Вход",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF1A73E8), // Синий цвет заголовка
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Поле для логина
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин/ID (Email)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8),

            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле для пароля
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Сообщение об ошибке
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Ошибка входа",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Сообщение об успешном входе
        if (successMessage != null) {
            Text(
                text = successMessage ?: "Успешный вход",
                color = Color(0xFF4CAF50), // Зеленый цвет для успеха
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Ссылка "Я забыл пароль"
        ClickableText(
            text = AnnotatedString("Я забыл пароль"),
            onClick = { /* Реализация логики восстановления */ },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1A73E8), // Синий цвет текста
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Кнопка "Войти"
        Button(
            onClick = { performLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A73E8), // Синий фон кнопки
                contentColor = Color.White // Белый текст на кнопке
            ),
            enabled = !isLoading // Блокируем кнопку во время загрузки
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Войти")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ссылка "Регистрация"
        ClickableText(
            text = AnnotatedString("Регистрация"),
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)},
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF1A73E8), // Синий цвет текста
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
