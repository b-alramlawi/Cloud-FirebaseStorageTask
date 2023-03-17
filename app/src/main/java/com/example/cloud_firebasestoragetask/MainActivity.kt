package com.example.cloud_firebasestoragetask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import com.example.cloud_firebasestoragetask.ui.screen.MainScreen
import com.example.cloud_firebasestoragetask.ui.theme.CloudFirebaseStorageTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloudFirebaseStorageTaskTheme {
                Surface {
                    MainScreen()
                }
            }
        }
    }
}



