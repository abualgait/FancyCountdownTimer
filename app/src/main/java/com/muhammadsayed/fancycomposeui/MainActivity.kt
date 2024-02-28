package com.muhammadsayed.fancycomposeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muhammadsayed.fancycomposeui.ui.theme.FancyComposeUITheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


const val total = 100
const val itemsPerRulerHeight = 30
val fontSize = 60.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FancyComposeUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFEFF7FF)
                ) {
                    FancyCountDownTimer()
                }
            }
        }
    }
}

// TODO: still work in progress, it requires some fine-tuning and adjustments
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FancyCountDownTimer() {

    val scrollState = rememberLazyListState()
    val snappingLayout = remember(scrollState) { SnapLayoutInfoProvider(scrollState) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)
    val count by remember(scrollState) {
        derivedStateOf {
            (total - scrollState.firstVisibleItemIndex)
        }
    }

    val localDensity = LocalDensity.current

    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }

    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    var topEmptyCount by remember {
        mutableFloatStateOf(0f)
    }

    var bottomEmptyCount by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(columnHeightDp) {
        itemHeight = columnHeightDp / itemsPerRulerHeight
        topEmptyCount = ((columnHeightDp / 2f) / itemHeight)
        bottomEmptyCount = topEmptyCount - 10
    }

    var minutesState by remember {
        mutableIntStateOf(0)
    }

    var secondsState by remember {
        mutableIntStateOf(0)
    }

    var isRunning by remember {
        mutableStateOf(false)
    }



    LaunchedEffect(isRunning) {
        var minutes = count
        var seconds = 0

        while (minutes >= 0 && isRunning) {
            minutesState = minutes
            secondsState = seconds
            delay(1000)
            if (seconds == 0) {
                minutes--
                seconds = 59
            } else {
                seconds--
            }
        }

    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            text = "Countdown Timer",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF73849C)
        )

        if (!isRunning) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AnimatedCounter(
                    count
                )
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize,
                    text = ":00",
                    style = MaterialTheme.typography.headlineLarge,
                    softWrap = false, color = Color(0xFF429CFF),
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AnimatedCounter(
                    minutesState
                )
                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize,
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    softWrap = false, color = Color(0xFF429CFF),
                )
                AnimatedCounter(
                    secondsState

                )

            }
        }
        Column(Modifier.weight(1f)) {

            Box(
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        // Set column height using the LayoutCoordinates
                        columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                    }) {

                LazyColumn(state = scrollState, flingBehavior = flingBehavior) {

                    items(topEmptyCount.toInt()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(itemHeight)
                        ) {

                            Spacer(modifier = Modifier.weight(1f))


                            Box(
                                Modifier
                                    .background(Color(0xD273849C))
                                    .width(20.dp)
                                    .height(1.dp)

                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    itemsIndexed(constructCounter()) { _, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(itemHeight)

                        ) {
                            if (item % 10 == 0 && (item in 10..total)) {
                                Text(
                                    fontWeight = FontWeight.Medium,
                                    text = item.toString(),
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    textAlign = TextAlign.Start,
                                    color = if (item == count) Color(0xFF429CFF) else Color(
                                        0xFF73849C
                                    ),
                                )

                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Box(
                                Modifier
                                    .background(Color(0xFF73849C))
                                    .width(20.dp)
                                    .height(1.dp)

                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }

                    }


                    items(bottomEmptyCount.toInt()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(itemHeight)

                        ) {

                            Spacer(modifier = Modifier.weight(1f))


                            Box(
                                Modifier
                                    .background(Color(0xFF73849C))
                                    .width(20.dp)
                                    .height(1.dp)

                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                }

                //top shadow
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(columnHeightDp.value.roundToInt().dp / 2)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFEFF7FF), Color.Transparent)
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                //bottom shadow
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(columnHeightDp.value.roundToInt().dp / 2)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFFEFF7FF))
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                //blue indicator
                Box(
                    Modifier
                        .offset(y = 1.dp)
                        .align(Alignment.Center)
                        .background(Color(0xFF429CFF))
                        .width(50.dp)
                        .height((2).dp)

                )


            }
        }


        //Start, Pause and Stop
        AnimatedVisibility(visible = isRunning) {
            Row(Modifier.fillMaxWidth()) {

                Text(
                    text = "Pause",
                    modifier = Modifier
                        .clickable {
                            isRunning = false
                        }
                        .background(Color(0xFF73849C))
                        .weight(1f)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )


                Text(
                    text = "Delete",
                    modifier = Modifier
                        .clickable {
                            isRunning = false
                        }
                        .background(Color(0xFFFF7373))
                        .weight(1f)
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
        AnimatedVisibility(visible = !isRunning) {
            Text(
                text = "Start",
                modifier = Modifier
                    .clickable {
                        isRunning = true
                    }
                    .background(Color(0xFF429CFF))
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center, color = Color.White
            )

        }


    }
}

/**
 * copyrights  https://github.com/philipplackner/AnimatedCounterCompose/blob/master/app/src/main/java/com/plcoding/animatedcountercompose/AnimatedCounter.kt
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    var oldCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }, label = ""
            ) { char ->

                Text(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize,
                    text = char.toString(),
                    style = style,
                    softWrap = false, color = Color(0xFF429CFF),
                )
            }
        }
    }
}

private fun constructCounter(): List<Int> {
    val list = mutableListOf<Int>()
    repeat(total) {
        list.add(total - it)
    }

    return list
}

@Preview
@Composable
fun FancyCountDownTimerPreview() {
    FancyComposeUITheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFEFF7FF)
        ) {
            FancyCountDownTimer()
        }
    }
}