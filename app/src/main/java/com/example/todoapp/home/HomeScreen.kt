@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.home

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.material3.TimeInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todoapp.NoteViewModel
import com.example.todoapp.NoteViewModelFactory
import com.example.todoapp.R
import com.example.todoapp.model.Day
import com.example.todoapp.model.NoteItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
//    val notes = listOf<NoteItem>(
//        NoteItem(noteTitle = "Play basket ball", noteTime = "10:30 AM"),
//        NoteItem(noteTitle = "Play football", noteTime = "11:30 AM"),
//        NoteItem(noteTitle = "Play volleyball", noteTime = "12:30 AM")
//    )
    val days = generateUpcomingDays()
    val coroutineScope = rememberCoroutineScope()
    var date by remember {
        mutableStateOf("2023-06-03")
    }
    val scaffoldState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(scaffoldState)
    val context = LocalContext.current

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(context.applicationContext as Application)
    )
//    val calendar = Calendar.getInstance()
    var noteTitle by remember {
        mutableStateOf("")
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
//        initialDisplayedMonthMillis = LocalDate.now().toEpochDay())
    val showDialog = remember { mutableStateOf(false) }
    val showTimeDialog = remember { mutableStateOf(false) }
    val addEnabled = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }

    var noteTime by remember {
        mutableStateOf("")
    }
    var formattedDate: String = ""
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 50.dp,
//        sheetShape = Large,
        sheetContent = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Add New Task",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )

            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextField(
                    value = noteTitle,
                    isError = isError.value,
                    onValueChange = {
                        noteTitle = it
                        if (it.trim().isNotEmpty() && it.trim().count() <= 15) {
                            addEnabled.value = true
                            isError.value = false
                        } else {
                            addEnabled.value = false
                            isError.value = true
                        }
                    },
                    supportingText = {
                        if (isError.value && noteTitle.trim().count() > 15) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Limit Is 15 Character",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (isError.value && noteTitle.trim().isEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Note title cannot be empty",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    placeholder = {
                        Text(text = "Enter Your Task")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Gray,

                        )
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    enabled = addEnabled.value,
                    onClick = {
                        if (addEnabled.value) {
                            showDialog.value = true
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.hide()
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Add Note",
                        color = Color.White
                    )
                }
                if (showDialog.value) {
                    DateDialog(
                        datePickerState = datePickerState,
                        onDismissRequest = { showDialog.value = false },
                        onDateSelected = {
                            val dateFormat =
                                SimpleDateFormat("yyyy-MM-dd") // Specify the desired date format
                            val date =
                                Date(it.toLong()) // Create a Date object from the milliseconds
                            formattedDate = dateFormat.format(date)
                            showTimeDialog.value = true
//                            noteViewModel.addNote(
//                                NoteItem(
//                                    noteTitle = noteDesc,
//                                    noteDate = formattedDate,
//                                    noteTime = ""
//                                )
//                            )
                        }
                    )
                }
                if (showTimeDialog.value) {
                    TimeDialog {
                        showTimeDialog.value = false
                        if (it != null) {
                            noteTime = it
                            noteViewModel.addNote(
                                NoteItem(
                                    noteTitle = noteTitle,
                                    noteDate = formattedDate,
                                    noteTime = noteTime,
                                    isDone = false
                                )
                            )
                        }
                        Log.d("timealaa2", noteTime)
                        Log.d("selDate", formattedDate)
                    }
                }
            }
        }
    ) {
        AllContent(days, coroutineScope, bottomSheetScaffoldState, noteViewModel, date)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(onDismissRequest: (String?) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    TimePickerDialog(
        onCancel = { onDismissRequest(null) },
        onConfirm = {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, state.hour)
            cal.set(Calendar.MINUTE, state.minute)
            cal.isLenient = false
            Log.d("timealaa", "Entered time: ${formatter.format(cal.time)}")
            onDismissRequest(formatter.format(cal.time))
        },
    ) {
        TimeInput(state = state)
    }

}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) { Text("Cancel", style = TextStyle(color = Color.Black)) }
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier.padding(start = 8.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) { Text("OK", style = TextStyle(color = Color.Black)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    datePickerState: DatePickerState, onDismissRequest: () -> Unit, onDateSelected: (String) -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    // Call the onDateSelected callback and pass the selected date
                    onDateSelected(datePickerState.selectedDateMillis.toString())
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                )
            ) {
                Text("Confirm", style = TextStyle(color = Color.Black))
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContentColor = Color.Black,
                selectedDayContainerColor = Color.Gray,
                todayContentColor = Color.Blue
            )
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllContent(
    days: List<Day>,
    coroutineScope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    noteViewModel: NoteViewModel,
    date: String
) {
//    val tz = TimeZone.getTimeZone("Egypt/Cairo")
//    val zoneId = ZoneId.of(tz.toZoneId().id)
//    val currentTime = Instant.now().atZone(zoneId).toEpochSecond()
//    Log.d("alaanow" , currentTime.toString())
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.List, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Settings, contentDescription = "")
                    }
                },
                contentColor = Color(0xFF5D9CEC),
                modifier = Modifier
                    .height(75.dp)
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        },
                        containerColor = Color(0xFFDBDADA)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "")
                    }
                },
//                containerColor = Color.White,
            )
        },
//        floatingActionButtonPosition = FabPosition.Center,

    ) { padding ->
        Modifier.padding(padding)
        HomeScreenContent(days, noteViewModel, date)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateUpcomingDays(): List<Day> {
    val days = mutableListOf<Day>()
    val formatter = DateTimeFormatter.ofPattern("dd")

    for (i in 0 until 7) {
        val currentDate = LocalDate.now().plusDays(i.toLong())
        val date = currentDate.format(formatter)
        Log.d("epoch", date)

        val dayOfWeek = currentDate.dayOfWeek.name.substring(0, 3)
        days.add(Day(i + 1, date, dayOfWeek))
    }

    return days
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpperPanel(days: List<Day>, date: String, noteViewModel: NoteViewModel) {
    val selectedItemId = remember {
        mutableStateOf(days.first().id)
    }
    Column {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.tertiary)
                .fillMaxHeight(.25f)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    //                .padding(start = 20.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Text(
                    text = "To Do List",
                    modifier = Modifier.padding(start = 25.dp, top = 40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(days) { day ->
                        DayCard(
                            day = day,
                            isSelected = selectedItemId.value == day.id,
                            onDateSelected = {
                                selectedItemId.value = day.id
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
                                val currentDate = LocalDate.now()
                                val newDate = currentDate.format(formatter)
//                                Log.d("datee" , date)
                                noteViewModel.updateDate("$newDate-$it")
                                Log.d("datee", "$newDate-$it")
                            },
                            date = date
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(note: NoteItem?, noteViewModel: NoteViewModel) {
//    val noteList by noteViewModel.noteList.observeAsState(listOf())
    Card(
        modifier = Modifier
            .padding(20.dp)
            .height(110.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Divider(
                color = if (note?.isDone == true) Color(0xFF61E757) else Color(0xff5D9CEC),
                modifier = Modifier
                    .fillMaxHeight(.7f)
                    .width(4.dp)
            )
            Column(
                Modifier.fillMaxHeight(),
//                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,

                ) {
                Text(
                    text = note?.noteTitle!!,
                    color = if (note.isDone) Color(0xFF61E757) else Color(0xff5D9CEC),
                    fontSize = 20.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = note.noteTime,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

            }
            Card(
                onClick = {
                    Log.d("noteId", note?.noteId.toString())
                    Log.d("donee", note?.isDone.toString())

                    noteViewModel.updateNote(
                        NoteItem(
                            noteId = note?.noteId!!,
                            noteTitle = note.noteTitle,
                            noteTime = note.noteTime,
                            noteDate = note.noteDate,
                            isDone = !note.isDone
                        )
                    )
//                    noteViewModel.getIsDone(note.noteId)
//                    isDone = !isDone!!
                },
                modifier = Modifier.fillMaxWidth(0.32f),
                colors = CardDefaults.cardColors(
                    containerColor = if (note?.isDone == true) Color.Transparent else MaterialTheme.colorScheme.tertiary
                )
            ) {
                if (note?.isDone == true) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Done!",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF61E757)
                        )
                    }
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.check_icon),
                            contentDescription = "check icon"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCard(
    day: Day,
    isSelected: Boolean,
    onDateSelected: (String) -> Unit,
    date: String
) {

    Card(
        onClick = {
            onDateSelected(day.date)
        },
        modifier = Modifier
            .height(90.dp)
            .width(70.dp)
            .padding(end = 8.dp),
        colors = CardDefaults.cardColors(
            contentColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(7.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = day.dayOfWeek,
                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = day.date,
                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LowerPanel(noteViewModel: NoteViewModel) {
    val date = noteViewModel.date.observeAsState().value
    noteViewModel.getNotesInDate(date!!)
    val notes = noteViewModel.noteList.observeAsState(listOf()).value
    LazyColumn(Modifier.fillMaxHeight()) {
        items(notes) { note ->
            NoteCard(note, noteViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenContent(
    days: List<Day>,
    noteViewModel: NoteViewModel,
    date: String
) {
    Column {
        UpperPanel(days, date, noteViewModel)
        LowerPanel(noteViewModel)
    }
}