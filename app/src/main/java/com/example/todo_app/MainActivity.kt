package com.example.todo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var tasks by remember { mutableStateOf(mutableListOf<Task>()) }
    var newTask by remember { mutableStateOf("") }
    var isTaskInputVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("To-Do List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isTaskInputVisible = !isTaskInputVisible
                },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                TaskList(
                    tasks = tasks,
                    onTaskComplete = { index, isCompleted -> tasks[index] = tasks[index].copy(isCompleted = isCompleted) },
                    onDeleteTask = { index -> tasks.removeAt(index) }
                )

                if (isTaskInputVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TaskInput(
                        task = newTask,
                        onTaskChange = { newTask = it },
                        onAddTask = {
                            if (newTask.isNotBlank()) {
                                tasks.add(Task(newTask, false))
                                newTask = ""
                                isTaskInputVisible = false // Hide input after adding the task
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TaskInput(task: String, onTaskChange: (String) -> Unit, onAddTask: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()  // Request focus as soon as the TaskInput is composed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()  // Adjust layout when the keyboard is visible
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            value = task,
            onValueChange = onTaskChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .focusRequester(focusRequester),  // Apply focusRequester to the TextField
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            ),
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
    onDeleteTask: (Int) -> Unit
) {
    Column {
        tasks.forEachIndexed { index, task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .background(color = Color.LightGray)
                    .clip(RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically // Center items vertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { isChecked -> onTaskComplete(index, isChecked) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                IconButton(onClick = { onDeleteTask(index) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TodoAppPreview() {
//    TodoappTheme {
//        TodoApp()
//    }
//}

@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    TodoappTheme {
        var tasks by remember { mutableStateOf(mutableListOf(
            Task("Buy groceries", isCompleted = false),
            Task("Finish Android project", isCompleted = true),
            Task("Exercise for 30 minutes", isCompleted = false)
        )) }

        TodoApp()
    }
}

