package com.example.timetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timetracker.ui.theme.TimeTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeTrackerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomAppBar(navController)
                    }
                ) { innerPadding ->
                    NavigationHost(navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BottomAppBar(navController: NavHostController) {
    val items = listOf("Task", "Calendar", "Notes", "Stats")
    val icons = listOf(Icons.Filled.Check, Icons.Filled.DateRange, Icons.Filled.Create, Icons.Filled.AccountCircle)
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item) },
                selected = currentRoute == item,
                onClick = {
                    navController.navigate(item) {
                        // Avoid multiple copies of the same destination
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                        // Avoid multiple instances of the same destination
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "Task", modifier = modifier) {
        composable("Task") { TaskScreen() }
        composable("Calendar") { CalendarScreen() }
        composable("Notes") { NoteScreen() }
        composable("Stats") { ProfileScreen() }
    }
}

@Composable
fun TaskScreen() {
    Text(
        text = "Welcome to the Home Screen!",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun CalendarScreen() {
    Text(
        text = "Here are your favorite items!",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun NoteScreen() {
    Text(
        text = "Notes Screen",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}


@Composable
fun ProfileScreen() {
    Text(
        text = "This is your profile!",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimeTrackerTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomAppBar(navController)
            }
        ) { innerPadding ->
            NavigationHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}
