package off.kys.remaindu.service.base

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ServiceCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import off.kys.remaindu.presentation.theme.RemainduTheme

abstract class BaseOverlayService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    protected lateinit var windowManager: WindowManager
    protected var composeView: ComposeView? = null

    abstract val notificationId: Int
    abstract val channelId: String
    abstract val channelName: String

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startAsForeground()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    protected abstract fun createNotification(): Notification

    private fun startAsForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }

        @SuppressLint("InlinedApi")
        ServiceCompat.startForeground(
            this,
            notificationId,
            createNotification(),
            foregroundServiceType()
        )
    }

    /**
     * Override this to specify the foreground service type if needed.
     */
    protected open fun foregroundServiceType(): Int = 0

    protected fun showOverlay(content: @Composable () -> Unit) {
        if (composeView != null) return

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            setupLayoutParams(this)
        }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@BaseOverlayService)
            setViewTreeViewModelStoreOwner(this@BaseOverlayService)
            setViewTreeSavedStateRegistryOwner(this@BaseOverlayService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

            setContent {
                RemainduTheme {
                    content()
                }
            }
        }

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        windowManager.addView(composeView, params)
    }

    /**
     * Optional hook to customize layout parameters.
     */
    protected open fun setupLayoutParams(params: WindowManager.LayoutParams) {}

    protected fun removeOverlay() {
        composeView?.let { view ->
            if (view.isAttachedToWindow) {
                windowManager.removeView(view)
            }
            composeView = null
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}
