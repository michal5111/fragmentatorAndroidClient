package com.example.springfragmenterclient

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.springfragmenterclient.component.DaggerAppComponent
import com.example.springfragmenterclient.modules.AppModule
import com.example.springfragmenterclient.rest.responses.ErrorResponse
import com.google.gson.Gson
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import retrofit2.HttpException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AcraCore(
    buildConfigClass = BuildConfig::class,
    reportFormat = StringFormat.JSON,
)
@AcraHttpSender(
    uri = "http://acrarium.mkubiak.it/report",
    basicAuthLogin = "yyzEfXxXlEmIRDI2",
    basicAuthPassword = "213DiUw6vVxnX4Zs",
    httpMethod = HttpSender.Method.POST
)
class Fragmentator4000 : DaggerApplication() {

    @Inject
    lateinit var gson: Gson

    companion object {
        private const val serverUrl = "http://michal5111.ddns.net:8080/fragmentatorServer"
        const val apiUrl = "$serverUrl/api"
        const val fragmentsUrl = "$serverUrl/fragments"

        fun timeToSeconds(time: String): Double {
            val split = time.split(":")
            if (split.size < 3) return 0.0
            val hours = split[0].toDouble()
            val minutes = split[1].toDouble()
            val seconds = split[2].toDouble()
            return hours * 3600 + minutes * 60 + seconds
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun encodeValue(value: String): String {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
        }

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

    fun errorHandler(throwable: Throwable) {
        Log.e("FragmenterError", throwable.message, throwable)
        var errorMessage = "Error ${throwable.message}"
        if (throwable is HttpException) {
            val errorJson = throwable.response()?.errorBody()?.string()
            if (errorJson != null) {
                val errorStatus = throwable.code()
                if (errorJson.isNotBlank()) {
                    try {
                        val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)
                        errorMessage = errorResponse.message
                    } catch (e: Exception) {
                    }
                }
                Toast.makeText(
                    this, getString(
                        R.string.httpError,
                        errorStatus,
                        errorMessage
                    ), Toast.LENGTH_LONG
                )
                    .show()
            }
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent: com.example.springfragmenterclient.component.AppComponent =
            DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        appComponent.inject(this)
        return appComponent
    }
}