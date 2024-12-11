package com.example.jet

import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.jet.model.RegisterRequest
import com.example.jet.model.RegisterResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun performRegister(context: Context) {
        isLoading = true
        errorMessage = null
        successMessage = null
        if (password.length < 8 || password.length > 100) {
            errorMessage = "Пароль должен быть от 8 до 100 символов"
            isLoading = false
            return
        }
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val registerRequest = RegisterRequest(
            first_name = firstName.trim(),
            last_name = lastName.trim(),
            email = email.trim(),
            password = password
        )

        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    successMessage = registerResponse?.message ?: "Регистрация прошла успешно"
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                    // Переход на LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
                else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = try {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("message") // Извлекаем сообщение об ошибке из JSON
                    } catch (e: Exception) {
                        "Неизвестная ошибка: ${response.code()}"
                    }
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Сбой подключения: ${t.message}"
            }
        })
    }
    @Composable
    fun ShowToast(message: String) {
        val context = LocalContext.current
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFFFFFFF)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Логотип
        Image(
            painter = painterResource(id = R.drawable.logo), // Ваш логотип
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1.5f)
                .padding(bottom = 32.dp)
        )

        // Заголовок
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF1A73E8),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Поля для регистрации (имя, фамилия, email, пароль)
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it.trim() },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it.trim() },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Логин/ID (Email)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.trim() },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1A73E8),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF1A73E8),
                focusedLabelColor = Color(0xFF1A73E8)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { performRegister(context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A73E8),
                contentColor = Color.White
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Регистрация")
            }
        }
    }
}

