package com.rahulyadav.analyticslib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahulyadav.analytics.analytics.AnalyticImp
import com.rahulyadav.analyticslib.ui.theme.AnalyticsLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Analytics is auto-initialized via ContentProvider, just set the API key
        AnalyticImp.init("your-api-key-here")
        
        setContent {
            AnalyticsLibTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnalyticsDemo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Analytics Library Demo",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                AnalyticImp.logEvent(
                    "button_clicked",
                    mapOf(
                        "button_name" to "demo_button",
                        "timestamp" to System.currentTimeMillis()
                    )
                )
            }
        ) {
            Text("Log Event")
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsDemoPreview() {
    AnalyticsLibTheme {
        AnalyticsDemo()
    }
}