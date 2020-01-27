package com.example.springfragmenterclient

import android.app.Activity
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
import retrofit2.HttpException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class Fragmentator4000 : DaggerApplication() {

    private val gson: Gson = Gson()

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

    fun errorHandler(throwable: Throwable) {
        Log.e("FragmenterError", throwable.message, throwable)
        if (throwable is HttpException) {
            val errorJson = throwable.response()?.errorBody()?.string()
            if (errorJson != null) {
                if (errorJson.isNotBlank()) {
                    val errorResponse =
                        gson.fromJson(errorJson, ErrorResponse::class.java)
                    Toast.makeText(this, getString(
                        R.string.httpError,
                        errorResponse.status,
                        errorResponse.message
                    ), Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            Toast.makeText(this, "error ${throwable.localizedMessage}", Toast.LENGTH_LONG)
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