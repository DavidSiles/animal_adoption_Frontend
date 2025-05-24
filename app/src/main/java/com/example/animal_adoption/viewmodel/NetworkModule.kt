package com.example.animal_adoption.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.text.format.Formatter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.animal_adoption.screens.widgets.SplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Socket

object NetworkModule {
    private const val DEFAULT_PORT = "8080"
    private const val EMULATOR_IP = "10.0.2.2"
    private const val DEFAULT_BACKEND_IP = "192.168.1.41" // Fallback IP
    private const val PREFS_NAME = "NetworkPrefs"
    private const val KEY_BACKEND_IP = "backend_ip"
    private const val SCAN_TIMEOUT_MS = 1000 // Timeout for each IP scan
    @Volatile
    private var retrofit: Retrofit? = null

    suspend fun provideRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val baseUrl = getBaseUrl(context)
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private suspend fun getBaseUrl(context: Context): String {
        if (isEmulator()) {
            return "http://$EMULATOR_IP:$DEFAULT_PORT/"
        }

        // Check cached backend IP
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedIp = prefs.getString(KEY_BACKEND_IP, null)
        if (cachedIp != null && isServerReachable(cachedIp)) {
            return "http://$cachedIp:$DEFAULT_PORT/"
        }

        // Scan subnet for backend IP
        val backendIp = scanSubnetForBackend(context)
        if (backendIp != null) {
            prefs.edit().putString(KEY_BACKEND_IP, backendIp).apply()
            return "http://$backendIp:$DEFAULT_PORT/"
        }

        // Fallback to default IP
        return "http://$DEFAULT_BACKEND_IP:$DEFAULT_PORT/"
    }

    private fun isEmulator(): Boolean {
        return (android.os.Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
                android.os.Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
                android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MANUFACTURER.contains("Genymotion"))
    }

    private suspend fun scanSubnetForBackend(context: Context): String? = withContext(Dispatchers.IO) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        if (ipAddress == "0.0.0.0") {
            return@withContext null
        }

        // Extract subnet (e.g., "192.168.1" from "192.168.1.56")
        val subnet = ipAddress.substringBeforeLast(".")
        val ipRange = (1..254).map { "$subnet.$it" }

        // Scan IPs in parallel
        val deferredResults = ipRange.map { ip ->
            async {
                if (isServerReachable(ip)) ip else null
            }
        }

        // Wait for the first valid IP
        val foundIp = deferredResults.awaitAll().filterNotNull().firstOrNull()
        return@withContext foundIp
    }

    private fun isServerReachable(ip: String): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, DEFAULT_PORT.toInt()), SCAN_TIMEOUT_MS)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun saveBackendIp(context: Context, ip: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BACKEND_IP, ip).apply()
        // Reset retrofit to force reinitialization with new IP
        retrofit = null
    }

    suspend inline fun <reified T> createService(context: Context): T {
        return provideRetrofit(context).create(T::class.java)
    }

    @Composable
    fun WithServiceInitialization(
        isServiceInitialized: Boolean,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        if (!isServiceInitialized) {
            SplashScreen(
                modifier = modifier,
            )
        } else {
            content()
        }
    }
}