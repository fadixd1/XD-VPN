package com.fadixd.fadivpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.fadixd.fadivpn.vpn.FadiVpnService
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BACKGROUND = Color(0xFF05080F)
private val CARD = Color(0xFF0D1420)
private val CARD_BORDER = Color(0xFF1B2A3D)
private val BLUE = Color(0xFF168CFF)
private val BLUE_LIGHT = Color(0xFF52B6FF)
private val GREEN = Color(0xFF35E878)
private val WHITE = Color(0xFFF5F8FC)
private val GRAY = Color(0xFF8794A8)

class MainActivity : ComponentActivity() {

    companion object {
        private const val VPN_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FadiVpnApp(
                onConnect = {
                    requestVpnAndStart()
                },
                onDisconnect = {
                    stopFadiVpn()
                }
            )
        }
    }

    private fun requestVpnAndStart() {
        val prepareIntent = VpnService.prepare(this)

        if (prepareIntent != null) {
            startActivityForResult(
                prepareIntent,
                VPN_REQUEST_CODE
            )
        } else {
            startFadiVpn()
        }
    }

    private fun startFadiVpn() {
        val intent = Intent(
            this,
            FadiVpnService::class.java
        ).setAction(FadiVpnService.ACTION_START)

        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopFadiVpn() {
        val intent = Intent(
            this,
            FadiVpnService::class.java
        ).setAction(FadiVpnService.ACTION_STOP)

        startService(intent)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == VPN_REQUEST_CODE &&
            resultCode == RESULT_OK
        ) {
            startFadiVpn()
        }
    }
}

@Composable
fun FadiVpnApp(
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {

    var connected by remember {
        mutableStateOf(false)
    }

    var seconds by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(connected) {

        if (connected) {

            while (true) {
                delay(1000)
                seconds++
            }

        } else {
            seconds = 0
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF03060B),
                            BACKGROUND,
                            Color(0xFF08111C)
                        )
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 28.dp,
                        bottom = 12.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BLUE.copy(alpha = 0.40f),
                                    BLUE.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(
                                elevation = 25.dp,
                                shape = CircleShape
                            )
                            .background(
                                Color(0xFF0A1727),
                                CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = BLUE.copy(alpha = 0.75f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "FADI VPN Shield",
                            tint = BLUE_LIGHT,
                            modifier = Modifier.size(43.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "FADI - VPN",
                    color = WHITE,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "SECURE • FAST • PRIVATE",
                    color = GRAY,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = if (connected) {
                        "CONNECTED"
                    } else {
                        "DISCONNECTED"
                    },
                    color = if (connected) GREEN else WHITE,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = formatTime(seconds),
                    color = GRAY,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(17.dp))

                Box(
                    modifier = Modifier
                        .size(275.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BLUE.copy(alpha = 0.35f),
                                    BLUE.copy(alpha = 0.18f),
                                    BLUE.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(218.dp)
                            .shadow(
                                elevation = 30.dp,
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        BLUE_LIGHT,
                                        BLUE,
                                        Color(0xFF0759A8)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF122A42),
                                        Color(0xFF09131F),
                                        Color(0xFF050B13)
                                    )
                                ),
                                CircleShape
                            )
                            .clickable {

                                if (connected) {
                                    onDisconnect()
                                    connected = false
                                } else {
                                    onConnect()
                                    connected = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (connected) {
                                    GREEN
                                } else {
                                    BLUE_LIGHT
                                },
                                modifier = Modifier.size(43.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (connected) {
                                    "CONNECTED"
                                } else {
                                    "CONNECT"
                                },
                                color = WHITE,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (connected) {
                                    "TAP TO DISCONNECT"
                                } else {
                                    "TAP TO CONNECT"
                                },
                                color = GRAY,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {

                    SpeedCard(
                        title = "UPLOAD",
                        value = if (connected) "0.0" else "--",
                        unit = "Mbps",
                        modifier = Modifier.weight(1f)
                    )

                    SpeedCard(
                        title = "DOWNLOAD",
                        value = if (connected) "0.0" else "--",
                        unit = "Mbps",
                        modifier = Modifier.weight(1f)
                    )

                    SpeedCard(
                        title = "PING",
                        value = if (connected) "42" else "--",
                        unit = "ms",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "تم صنع التطبيق بواسطة FADI - XD",
                    color = GREEN,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}

@Composable
fun SpeedCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier
            .height(82.dp)
            .border(
                width = 1.dp,
                color = CARD_BORDER,
                shape = RoundedCornerShape(17.dp)
            ),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = CARD
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 5.dp,
                    vertical = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                color = GRAY,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    text = value,
                    color = WHITE,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = unit,
                    color = GRAY,
                    fontSize = 8.sp
                )
            }
        }
    }
}

fun formatTime(total: Long): String {

    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60

    return "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds
    )
}
