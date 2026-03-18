package com.fashiondilse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fashiondilse.app.ui.FashionDilSeApp
import com.fashiondilse.app.ui.theme.FashionDilSeTheme
import com.fashiondilse.app.ui.theme.LocalThemeState
import com.fashiondilse.app.ui.theme.ThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeState = remember { ThemeState() }
            CompositionLocalProvider(LocalThemeState provides themeState) {
                FashionDilSeTheme(themeState = themeState) {
                    FashionDilSeApp()
                }
            }
        }
    }
}
