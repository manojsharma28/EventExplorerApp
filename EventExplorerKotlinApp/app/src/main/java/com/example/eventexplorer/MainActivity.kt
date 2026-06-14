package com.example.eventexplorer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventexplorer.ui.theme.EventExplorerTheme
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class EventItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val severity: String = "Normal",
    val source: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventExplorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EventExplorerScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventExplorerScreen() {
    val context = LocalContext.current
    val events = remember { mutableStateListOf<EventItem>() }
    val selectedEvents = remember { mutableStateListOf<String>() }
    var status by remember { mutableStateOf("Connecting...") }
    val brokerUrl = "tcp://10.0.2.2:1883"
   // val brokerUrl = "tcp://broker.hivemq.com:1883"
    val topic = "events"
    val mqttClient = remember(context) {
        MqttAndroidClient(context, brokerUrl, "eventexplorer-${UUID.randomUUID()}")
    }

    DisposableEffect(mqttClient) {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                status = if (reconnect) "Reconnected" else "Connected"
                mqttClient.subscribe(topic, 0)
            }

            override fun connectionLost(cause: Throwable?) {
                status = "Connection lost"
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val payload = message?.toString().orEmpty()
                try {
                    val json = JSONObject(payload)
                    events.add(0, EventItem(
                        id = json.optString("id", UUID.randomUUID().toString()),
                        title = json.optString("title", "No Title"),
                        description = json.optString("description", "No Description"),
                        timestamp = json.optString("date", formattedTimestamp()),
                        severity = json.optString("severity", "Normal"),
                        source = json.optString("source", "Unknown")
                    ))
                } catch (e: Exception) {
                    events.add(0, EventItem(
                        id = UUID.randomUUID().toString(),
                        title = "Plain Event",
                        description = payload,
                        timestamp = formattedTimestamp()
                    ))
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        try {
            val options = MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
            }

            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    status = "Connected"
                    mqttClient.subscribe(topic, 0)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    status = "Unable to connect"
                    Toast.makeText(context, "MQTT connection failed", Toast.LENGTH_LONG).show()
                }
            })
        } catch (exception: Exception) {
            status = "Connection error"
            Toast.makeText(context, "MQTT error: ${exception.message}", Toast.LENGTH_LONG).show()
        }

        onDispose {
            try {
                mqttClient.unregisterResources()
                mqttClient.disconnect()
            } catch (_: Exception) {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Event Explorer") },
            modifier = Modifier.fillMaxWidth(),
            actions = {
                if (selectedEvents.isNotEmpty()) {
                    IconButton(onClick = {
                        events.removeAll { it.id in selectedEvents }
                        selectedEvents.clear()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Selected",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )

        Text(
            text = "Status: $status",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )

        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Waiting for events...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        isSelected = event.id in selectedEvents,
                        onSelectionChange = { isSelected ->
                            if (isSelected) {
                                selectedEvents.add(event.id)
                            } else {
                                selectedEvents.remove(event.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: EventItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    val severityColor = when (event.severity.lowercase()) {
        "critical" -> Color(0xFFD32F2F)
        "warning" -> Color(0xFFFBC02D)
        "info" -> Color(0xFF1976D2)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = severityColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
            
            Column(modifier = Modifier
                .weight(1f)
                .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = severityColor
                    )
                    Text(
                        text = event.severity.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = severityColor
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${event.source} • ${event.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun formattedTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}
