package com.example.jet
import android.R.attr.action
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.jet.connection.RetrofitClient
import com.example.jet.model.ApiService
import com.example.jet.model.User
import com.google.android.material.internal.NavigationMenu
import okhttp3.Call
import okhttp3.Response
import javax.security.auth.callback.Callback

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarWithMenuScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarWithMenuScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isMenuVisible by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }


    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("authToken", null)


    if (token != null) {
        val call = apiService.getCurrentUser("Bearer $token")
        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                } else {

                }
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {

            }
        })
    }



    if (isMenuVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .clickable { isMenuVisible = false }
        ) {
            NavigationMenu(
                context = LocalContext.current, // Pass the current context
                currentUser = currentUser,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationMenu(
                context = LocalContext.current, // Pass the current context
                currentUser = currentUser,
                onClose = { scope.launch { drawerState.close() } }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = { Text("Календарь", fontWeight = FontWeight.Bold,
                            color = Color(0xFF267AFC)) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open() // Открыть меню
                                }
                            }) {
                                Icon(Icons.Filled.Menu, tint = Color(0xFF267AFC), contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    MonthSelector(
                        currentMonth = selectedMonth,
                        onMonthChanged = { selectedMonth = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CalendarGrid(
                        yearMonth = selectedMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )
                }
            }
        }
    )
}

@Composable
fun NavigationMenu(
    context: Context,
    currentUser: User?,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section of the menu
        Column {
            Image(
                painter = painterResource(id = R.drawable.logo_vis),
                contentDescription = "Jet Logistics Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Define menu items
            val menuItems = mutableListOf(
                Pair(painterResource(id = R.drawable.person), "Профиль") to {
                    val intent = Intent(context, ProfileActivity::class.java)
                    context.startActivity(intent)
                },
                Pair(painterResource(id = R.drawable.calendar), "Расписание") to {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                Pair(painterResource(id = R.drawable.money), "Зарплата") to {
                    val intent = Intent(context, SalaryActivity::class.java)
                    context.startActivity(intent)
                },
                Pair(painterResource(id = R.drawable.not), "Уведомления") to {
                    val intent = Intent(context, NotificationsActivity::class.java)
                    context.startActivity(intent)
                }
            )

            if (currentUser?.position_name == "Admin") {
                menuItems.add(
                    Pair(painterResource(id = R.drawable.info), "Сотрудники") to {
                        val intent = Intent(context, EmployeesActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }


            // Render menu items
            menuItems.forEach { (item, action) ->
                val (icon, title) = item
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            action() // Trigger the navigation action
                            onClose() // Close the drawer
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = icon as Painter,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = Color(0xFF267AFC),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        // Bottom section of the menu (Settings, Logout)
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Add settings logic here */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки",
                    tint = Color(0xFF267AFC),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Настройки", fontSize = 16.sp, color = Color(0xFF267AFC))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Add logout logic here */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Выйти",
                    tint = Color(0xFF267AFC),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Выйти", fontSize = 16.sp, color = Color(0xFF267AFC))
            }
        }
    }
}



@Composable
fun MonthSelector(currentMonth: YearMonth, onMonthChanged: (YearMonth) -> Unit) {
    val months = (1..12).map { currentMonth.withMonth(it) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(months) { month ->
            val isSelected = month == currentMonth

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color(0xFF267AFC) else Color.Transparent
                    )
                    .clickable { onMonthChanged(month) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru")),
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color(0xFF267AFC),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}



@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val daysInMonth = (1..lastDayOfMonth.dayOfMonth).map { yearMonth.atDay(it) }
    val startDayOffset = firstDayOfMonth.dayOfWeek.value % 7

    // Days from the previous month
    val prevMonth = yearMonth.minusMonths(1)
    val lastDayPrevMonth = prevMonth.atEndOfMonth()
    val previousMonthDays = (1..startDayOffset).map { lastDayPrevMonth.dayOfMonth - startDayOffset + it }.map {
        prevMonth.atDay(it)
    }

    // Days from the next month
    val endDayOffset = 7 - (startDayOffset + daysInMonth.size) % 7
    val nextMonth = yearMonth.plusMonths(1)
    val nextMonthDays = (1..endDayOffset).map { nextMonth.atDay(it) }

    val fullDaysGrid = previousMonthDays + daysInMonth + nextMonthDays

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Header with week days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = if (day == "СБ" || day == "ВС") Color.Red else Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days grid
        fullDaysGrid.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isCurrentMonth = date.month == yearMonth.month
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                if (date == selectedDate) Color(0xFF267AFC) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { if (isCurrentMonth) onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Text for the date
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when {
                                    !isCurrentMonth -> Color.Gray
                                    date.dayOfWeek.value >= 5 && date.dayOfWeek.value <= 6 -> Color.Red
                                    else -> Color.Black
                                },
                                fontSize = 16.sp,
                                fontWeight = if (date == selectedDate) FontWeight.Bold else FontWeight.Normal
                            )

                            if (date == selectedDate) {
                                Icon(
                                    imageVector = Icons.Default.Check, // Галочка
                                    contentDescription = "Selected",
                                    tint = Color(0xFFFFFFFF), // Синий цвет галочки
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 4.dp) // Отступ от текста даты
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview

@Composable
private fun Prev(modifier: Modifier = Modifier ) {
    CalendarWithMenuScreen()
}