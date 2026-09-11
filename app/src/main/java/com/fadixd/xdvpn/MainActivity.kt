package com.fadixd.xdvpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.fadixd.xdvpn.ui.theme.XDVPNTheme
import com.fadixd.xdvpn.vpn.XDVpnService

class MainActivity : ComponentActivity() {

    private var status by mutableStateOf("Disconnected")

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                startVpn()
            } else {
                status = "Disconnected"
            }
        }

    private val receiver =
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                val value =
                    intent?.getStringExtra(
                        XDVpnService.EXTRA_STATUS
                    )

                if (!value.isNullOrEmpty()) {
                    status = value
                }
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(
                XDVpnService.ACTION_STATUS
            ),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        setContent {

            XDVPNTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF080B12)
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Text(
                            text = "XD VPN",
                            color = Color.White,
                            fontSize = 32.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(30.dp)
                        )

                        Button(
                            onClick = {

                                if (
                                    status == "Connected"
                                ) {
                                    stopVpn()
                                } else {
                                    requestVpn()
                                }
                            },
                            modifier =
                                Modifier.size(160.dp),
                            shape = CircleShape,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (
                                            status == "Connected"
                                        ) {
                                            Color(0xFFE53935)
                                        } else {
                                            Color(0xFF2979FF)
                                        }
                                )
                        ) {

                            Text(
                                text =
                                    if (
                                        status == "Connected"
                                    ) {
                                        "DISCONNECT"
                                    } else {
                                        "CONNECT"
                                    }
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(25.dp)
                        )

                        Text(
                            text = status,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }

    private fun requestVpn() {

        status = "جاري طلب إذن VPN..."

        val intent =
            VpnService.prepare(this)

        if (intent != null) {
            permissionLauncher.launch(intent)
        } else {
            startVpn()
        }
    }

    private fun startVpn() {

        status = "جاري الاتصال..."

        val intent =
            Intent(
                this,
                XDVpnService::class.java
            )

        ContextCompat.startForegroundService(
            this,
            intent
        )
    }

    private fun stopVpn() {

        val intent =
            Intent(
                this,
                XDVpnService::class.java
            ).apply {
                action =
                    XDVpnService.ACTION_STOP
            }

        startService(intent)

        status = "Disconnected"
    }

    override fun onDestroy() {

        try {
            unregisterReceiver(receiver)
        } catch (_: Exception) {
        }

        super.onDestroy()
    }
}
