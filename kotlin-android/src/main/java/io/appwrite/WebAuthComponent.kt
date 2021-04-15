package io.appwrite

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.appwrite.services.KeepAliveService
import kotlinx.coroutines.delay
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set

internal class WebAuthComponent {

    companion object : DefaultLifecycleObserver {
        private var suspended = false
        private val callbacks = mutableMapOf<String, (((Result<String>) -> Unit)?)>()

        override fun onResume(owner: LifecycleOwner) {
            callbacks.forEach { (_, danglingResultCallback) ->
                danglingResultCallback?.invoke(
                    Result.failure(IllegalStateException("User cancelled login"))
                )
            }
            callbacks.clear()
            suspended = false
        }

        suspend fun authenticate(
            activity: ComponentActivity,
            url: Uri,
            callbackUrlScheme: String,
            onComplete: ((Result<String>) -> Unit)?
        ) {
            callbacks.clear()

            val intent = CustomTabsIntent.Builder().build()
            val keepAliveIntent = Intent(activity, KeepAliveService::class.java)

            callbacks[callbackUrlScheme] = onComplete

            intent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.intent.putExtra("android.support.customtabs.extra.KEEP_ALIVE", keepAliveIntent)
            intent.launchUrl(activity, url)

            activity.runOnUiThread {
                activity.lifecycle.addObserver(this)
            }

            suspended = true
            while (suspended) {
                delay(200)
            }
        }

        fun onCallback(scheme: String, url: Uri) {
            callbacks.remove(scheme)?.invoke(
                Result.success(url.toString())
            )
            suspended = false
        }
    }
}