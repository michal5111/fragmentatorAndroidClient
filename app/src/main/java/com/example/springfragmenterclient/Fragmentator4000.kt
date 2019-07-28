package com.example.springfragmenterclient

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.google.gson.reflect.TypeToken
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class Fragmentator4000 : Application() {

    companion object {
        private const val serverUrl = "http://michal5111.asuscomm.com:8080"
        const val apiUrl = "$serverUrl/api"
        const val fragmentsUrl = "$serverUrl/fragments"
        val movieListType = object : TypeToken<List<Movie>>() {}.type!!
        val linesListType = object : TypeToken<List<Line>>() {}.type!!

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
}