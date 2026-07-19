package off.kys.remaindu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import off.kys.remaindu.presentation.screen.home.HomeScreen
import off.kys.remaindu.presentation.theme.RemainduTheme

import androidx.compose.animation.core.tween
import off.kys.remaindu.util.SmoothDuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemainduTheme {
                Navigator(screen = HomeScreen()) { navigator ->
                    SlideTransition(
                        navigator = navigator,
                        animationSpec = tween(SmoothDuration)
                    )
                }
            }
        }
    }
}
