package com.example.todo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_app.ui.theme.TodoappTheme

data class Task(val description: String, var isCompleted: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoappTheme {
                TodoApp()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    var tasks by remember { mutableStateOf(mutableStateListOf<Task>()) }
    var newTask by remember { mutableStateOf("") }
    var isTaskInputVisible by remember { mutableStateOf(false) }
    val categories = List(20) { "Category ${it + 1}" }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            var expanded by remember { mutableStateOf(false) }

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    TopNavCategories(
                        categories = categories,
                        onCategoryClick = { category ->
                            // Handle category click
                        },
                        onOverflowMenuClick = {
                            expanded = true
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "More options",
                            tint = Color.Black
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Menu Item 1") },
                            onClick = { /* Handle menu item 1 click */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Menu Item 2") },
                            onClick = { /* Handle menu item 2 click */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Menu Item 3") },
                            onClick = { /* Handle menu item 3 click */ }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isTaskInputVisible) { // Hide the plus sign when the input field is visible
                FloatingActionButton(
                    onClick = {
                        isTaskInputVisible = true
                    },
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task",
                        tint = Color.White
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TaskList(
                    tasks = tasks,
                    onTaskComplete = { index, isCompleted ->
                        tasks[index] = tasks[index].copy(isCompleted = isCompleted)
                    },
                    onDeleteTask = { index -> tasks.removeAt(index) },
                    modifier = Modifier.weight(1f)  // Make the list take up available space
                )

                if (isTaskInputVisible) {
                    TaskInput(
                        task = newTask,
                        onTaskChange = { newTask = it },
                        onAddTask = {
                            if (newTask.isNotBlank()) {
                                tasks.add(Task(newTask, false))
                                newTask = ""
                                isTaskInputVisible = false // Hide input after adding the task
                            }
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    )
}

@Composable
fun TopNavCategories(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    onOverflowMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Standard height for top app bar
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            items(categories.size) { index ->
                Text(
                    text = categories[index],
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(24.dp))
                        .clickable { onCategoryClick(categories[index]) }
                        .padding(horizontal = 8.dp, vertical = 0.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TaskInput(
    task: String,
    onTaskChange: (String) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()  // Request focus as soon as the TaskInput is composed
    }

        Row(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = task,
                onValueChange = onTaskChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequester),  // Apply focusRequester to the TextField
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAddTask()
                        focusManager.clearFocus()  // Hide the keyboard after task is added
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (task.isEmpty()) {
                            Text("Enter a task", style = MaterialTheme.typography.bodyLarge)
                        }
                        innerTextField()
                    }
                }
            )
            Button(onClick = onAddTask) {
                Text("Add")
            }
        }

}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskComplete: (Int, Boolean) -> Unit,
    onDeleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp) // Add padding around the entire task list
    ) {
        tasks.forEachIndexed { index, task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .background(color = Color.LightGray)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically // Center items vertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { isChecked -> onTaskComplete(index, isChecked) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                IconButton(onClick = { onDeleteTask(index) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    TodoappTheme {
        TodoApp()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TodoAppPreview() {
//    TodoappTheme {
//        var tasks by remember { mutableStateOf(mutableListOf(
//            Task("Buy groceries", isCompleted = false),
//            Task("Finish Android project", isCompleted = true),
//            Task("Exercise for 30 minutes", isCompleted = false)
//        )) }
//
//        TodoApp()
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun TaskInputPreview() {
//    TodoappTheme {
//        TaskInput(
//            task = "Enter a task",
//            onTaskChange = {},
//            onAddTask = {}
//        )
//    }
//}
