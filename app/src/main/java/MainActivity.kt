package com.example.metruyenchu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.metruyenchu.navigation.AppNavigation
import com.example.metruyenchu.ui.theme.MeTruyenChuTheme
import com.example.metruyenchu.api.RetrofitClient


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        RetrofitClient.initialize(applicationContext)

        super.onCreate(savedInstanceState)

        setContent {

            MeTruyenChuTheme {

                AppNavigation()
            }
        }
    }
}