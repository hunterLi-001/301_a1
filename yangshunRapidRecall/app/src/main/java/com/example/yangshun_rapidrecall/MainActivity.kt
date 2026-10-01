package com.example.yangshun_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangshun_rapidrecall.ui.theme.YangshunRapidRecallTheme
import kotlinx.coroutines.delay
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YangshunRapidRecallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RapidRecallApp()
                }
            }
            }
        }
    }


// add a data variable to store attempt information
data class Attempt(
    var userInput: String,
    var response: String,
    var sequenceLength: Int,
    var targetSequence: String,
    var isCorrect: Boolean,
    var timestamp: Date = Date()
)

//set a control to shift from starting info page to actual decision-making app interface
@Composable
fun MainControl(modifier: Modifier = Modifier) {
    //two stages: starting page or app interface
    var stage by remember { mutableStateOf(1) }
    var totalAttempts by remember { mutableStateOf(0) }
    var correctAttempts by remember { mutableStateOf(0) }

    var currentAttempt by remember { mutableStateOf<Attempt?>(null) }
    // STORE ATTEMPT DATA IN A LIST HERE
    var attemptList by remember { mutableStateOf(listOf<Attempt>()) }

    if (stage == 1) {
        MenuScreen(onSelectedOption = {option -> stage = option},
            modifier = Modifier)
        //if
    } else if (stage == 2) {
        GameScreen(onSelectedOption = {option -> stage = option},
            onAttemptEnd = {attempt ->  currentAttempt = attempt
                           attemptList = attemptList + attempt
                           totalAttempts += 1
                           correctAttempts += if (attempt.isCorrect) 1 else 0},
            modifier = Modifier)
    } else if (stage == 3) {
        ResultsScreen(onSelectedOption = {option -> stage = option},
            attempt = currentAttempt,
            modifier = Modifier)
    } else if (stage == 4) {
        LoggingScreen(onSelectedOption = {option -> stage = option},
            attempt = attemptList,
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            modifier = Modifier)
    } else if (stage == 5) {
        AttemptScreen(onSelectedOption = {option -> stage = option},
            attempt = currentAttempt,
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            modifier = Modifier)
    }

//fun MainControl
}

@Composable
fun RapidRecallApp() {
    MainControl()
}

// menu screen
@Composable
fun MenuScreen(
    onSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(top =200.dp, start = 20.dp, end = 20.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Rapid Recall",
            fontSize = 30.sp,
            lineHeight = 45.sp,
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 300.dp),
        ) {

        Button(
            modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth(),
            onClick = {onSelectedOption(2)}
        ) {
            Text("New Game")
        }
            Button(
                modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth(),
                onClick = {onSelectedOption(3)}
            ) {
                Text("Results")
            }

            Button(
                modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth(),
                onClick = {onSelectedOption(4)}
            ) {
                Text("Logging")
            }

            Button(
                modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth(),
                onClick = {onSelectedOption(5)}
            ) {
                Text("Attempts")
            }
            // inner column
    }

        //outer column
    }
}


enum class GameStage {
    input,
    display,
    recall
}

@Composable
fun GameScreen(
    onAttemptEnd: (Attempt) -> Unit,
    onSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var gameStage by remember { mutableStateOf(GameStage.input) }
    var userInput by remember { mutableStateOf("0") }
    var targetSequence by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    var userResponse by remember { mutableStateOf("") }

    //display the target sequence
    LaunchedEffect(gameStage, currentIndex) {
        if(gameStage == GameStage.display) {
            if (currentIndex < targetSequence.length) {
                delay(1000)
                currentIndex++
            } else {
                gameStage = GameStage.recall
            }
        }
    }
    Column(
        modifier = Modifier.padding(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 20.dp).fillMaxSize()
    ) {
        if (gameStage == GameStage.input) {
            Text(text = "New Game Setup", fontSize = 40.sp)
            Spacer(modifier = Modifier.padding(10.dp))

            OutlinedTextField(
                value = userInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) userInput = it },
                label = { Text("Enter the sequence length") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))

            //button to start the game
            Button(
                onClick = {
                    val digits = userInput.toInt()
                    if (digits in 1..10) {
                        targetSequence = (1..digits).map { (0..9).random() }.joinToString("")
                        currentIndex = 0
                        gameStage = GameStage.display
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Text("Start Game")
            }
        } else if (gameStage == GameStage.display) {
            Text(text = "Display the target sequence", fontSize = 30.sp)
            Spacer(modifier = Modifier.padding(10.dp))

            if (currentIndex < targetSequence.length) {
                //indicate the current index of the target sequence
                Text(
                    text = "Current Index Is ${currentIndex +1} of ${targetSequence.length}",
                    fontSize = 30.sp
                )

                Text(
                    text = targetSequence[currentIndex].toString(),
                    fontSize = 60.sp
                )
            }
        //else if  display
        } else if (gameStage == GameStage.recall) {
            Text(text = "Recall the sequence", fontSize = 40.sp)
            Spacer(modifier = Modifier.padding(10.dp))

            OutlinedTextField(
                value = userResponse,
                onValueChange = {  if (it.all { char -> char.isDigit() }) userResponse = it },
                label = { Text("Enter your response") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = {
                    var attempt = Attempt(
                        userInput = userInput,
                        response = userResponse,
                        sequenceLength = targetSequence.length,
                        targetSequence = targetSequence,
                        isCorrect = userResponse == targetSequence,
                        timestamp = Date()
                    )
                    // go to results screen
                    onSelectedOption(3)
                    onAttemptEnd(attempt)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }


            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {onSelectedOption(1) }){
                Text("Back to Menu")
            }
        //else if recall
        }
    //column
    }
// Game screen
}

@Composable
fun ResultsScreen(
    attempt: Attempt?,
    onSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    //display the results
    Column(
        modifier = Modifier.padding(top = 60.dp).fillMaxSize()
    ) {
        Text(text = "Results", fontSize = 60.sp)
        Spacer(modifier = Modifier.padding(10.dp))

        if (attempt != null) {
            Text(text = "Sequence Length: ${attempt.sequenceLength}", fontSize = 30.sp)
            Text(text = "User Response: ${attempt.response}", fontSize = 30.sp,lineHeight = 40.sp)
            Text(text = "Target Sequence: ${attempt.targetSequence}", fontSize = 30.sp,lineHeight = 40.sp)
            Text(text = "Is Correct: ${attempt.isCorrect}", fontSize = 30.sp)
        } else {
            Text(text = "No attempt data available.", fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = {onSelectedOption(1) }){
            Text("Back to Menu")
        }
    }



// results screen
}

@Composable
fun AttemptScreen(
    attempt: Attempt?,
    totalAttempts: Int,
    correctAttempts: Int,
    onSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(top = 60.dp).fillMaxSize()
    ) {
        Text(text = "Summary", fontSize = 60.sp)
        Spacer(modifier = Modifier.padding(10.dp))

        if (attempt != null) {
            Text(text = "No. of Attempts: ${totalAttempts}", fontSize = 30.sp)
            Text(text = "No. of Correct Attempts: ${correctAttempts}", fontSize = 30.sp)
            Text(text = "Overall Accuracy: ${correctAttempts.toFloat() / totalAttempts.toFloat() * 100}%", fontSize = 30.sp)
        } else {
            Text(text = "No summary data available.", fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = {onSelectedOption(1) }){
            Text("Back to Menu")
        }
    }


}

@Composable
fun LoggingScreen(
    attempt: List<Attempt>?,
    totalAttempts: Int,
    correctAttempts: Int,
    onSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(top = 60.dp).fillMaxSize()
    ) {
        Text(text = "Logging Page", fontSize = 50.sp)
        Spacer(modifier = Modifier.padding(10.dp))

        if (!attempt.isNullOrEmpty()) {
            Text(text = "Attempt Logs", fontSize = 30.sp)
            Spacer(modifier = Modifier.padding(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(attempt) { attempt ->
                    Text(text = "Sequence Length: ${attempt.sequenceLength}", fontSize = 30.sp)
                    Text(text = "User Response: ${attempt.response}", fontSize = 30.sp,lineHeight = 40.sp)
                    Text(text = "Target Sequence: ${attempt.targetSequence}", fontSize = 30.sp,lineHeight = 40.sp)
                    Text(text = "Is Correct: ${attempt.isCorrect}", fontSize = 30.sp)
                }
            }
        } else {
            Text(text = "No logging data available.", fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = {onSelectedOption(1) }){
            Text("Back to Menu")
        }
    // column
    }
// logging screen
}

