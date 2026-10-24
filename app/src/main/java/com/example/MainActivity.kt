package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.repository.FitAdminRepository
import com.example.ui.navigation.FitAdminApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FitAdminViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FitAdminViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = FitAdminRepository(database)
        FitAdminViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FitAdminApp(viewModel = viewModel)
                }
            }
        }
    }
}
