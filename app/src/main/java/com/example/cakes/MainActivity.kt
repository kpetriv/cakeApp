package com.example.cakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.cakes.presentation.CakeListScreen
import com.example.cakes.ui.theme.CakesTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CakesTheme {
				CakeListScreen(modifier = Modifier.fillMaxSize())
			}
		}
	}
}
