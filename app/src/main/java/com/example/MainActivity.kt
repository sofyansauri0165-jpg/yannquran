package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.QuranViewModel
import com.example.ui.YannQuranApp
import com.example.ui.theme.ForestDark
import com.example.ui.theme.YannQuranTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YannQuranTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ForestDark
                ) {
                    val quranViewModel: QuranViewModel = viewModel()
                    YannQuranApp(viewModel = quranViewModel)
                }
            }
        }
    }
}
