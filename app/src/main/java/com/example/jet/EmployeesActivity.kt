package com.example.jet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jet.connection.RetrofitClient
import com.example.jet.model.ApiService
import com.example.jet.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import android.os.Environment
class EmployeesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmployeesScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen() {
    val users = remember { mutableStateListOf<User>() }
    val context = LocalContext.current

    // Fetch data when the composable is launched
    LaunchedEffect(Unit) {
        fetchUsers(context, users)
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text("Employees", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { exportToExcel(context, users) },
                containerColor = Color(0xFF267AFC)
            ) {
                Icon(Icons.Filled.Download, contentDescription = "Export to Excel", tint = Color.White)
            }

        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center // Centers content both horizontally and vertically
        ) {
            if (users.isEmpty()) {
                Text("Loading users...", fontSize = 18.sp)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users) { user ->
                        EmployeeCard(
                            firstName = user.first_name.toString(),
                            lastName = user.last_name.toString(),
                            email = user.email.toString(),
                            positionName = user.position_name.toString()
                        )
                    }
                }
            }
        }
    }
}

suspend fun fetchUsers(context: Context, users: MutableList<User>) {
    withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)

        if (authToken != null) {
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.getAllUser("Bearer $authToken").execute()
                if (response.isSuccessful) {
                    response.body()?.let { userList ->
                        users.clear()
                        users.addAll(userList) // Correctly add all users from the response
                    }
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Authorization token is missing.")
        }
    }
}

@Composable
fun EmployeeCard(
    firstName: String?,
    lastName: String?,
    email: String?,
    positionName: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "First Name: ${firstName ?: "Unknown"}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Last Name: ${lastName ?: "Unknown"}",
                fontSize = 16.sp
            )
            Text(
                text = "Email: ${email ?: "Unknown"}",
                fontSize = 16.sp
            )
            Text(
                text = "Position: ${positionName ?: "Unknown"}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}




fun exportToExcel(context: Context, users: List<User>) {
    val workbook = HSSFWorkbook()
    val sheet = workbook.createSheet("Employees")

    // Create header row
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("First Name")
    headerRow.createCell(1).setCellValue("Last Name")
    headerRow.createCell(2).setCellValue("Email")
    headerRow.createCell(3).setCellValue("Position")

    // Populate data
    users.forEachIndexed { index, user ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(user.first_name ?: "Unknown")
        row.createCell(1).setCellValue(user.last_name ?: "Unknown")
        row.createCell(2).setCellValue(user.email ?: "Unknown")
        row.createCell(3).setCellValue(user.position_name ?: "Unknown")
    }

    // Save to Downloads folder
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "Employees.xls")
        FileOutputStream(file).use { fos ->
            workbook.write(fos)
        }
        workbook.close()

        // Show a Toast with the file location
        Toast.makeText(context, "Excel file saved in Downloads: ${file.absolutePath}", Toast.LENGTH_LONG).show()


    } catch (e: Exception) {
        e.printStackTrace()
        // Show an error message in case of failure
        Toast.makeText(context, "Failed to create Excel file: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun openExcelFile(context: Context, file: File) {
    try {
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/vnd.ms-excel")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Open Excel File"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "No application found to open the Excel file.", Toast.LENGTH_LONG).show()
    }
}


